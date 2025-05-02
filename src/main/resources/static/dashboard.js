let currentWeatherData = null;

document.addEventListener("DOMContentLoaded", () => {
    const username = loadUsernameFromTokenTable();
    const selectedLocationParam = new URLSearchParams(window.location.search).get('location');

    const select = document.getElementById("locationSelect");
    const locationDisplay = document.getElementById("locationDisplay");

    fetch(`/locations/user/${username}`)
        .then(res => res.json())
        .then(locations => {
            if (locations.length === 0) {
                select.innerHTML = '<option disabled>No locations</option>';
                locationDisplay.textContent = "No location selected";
                return;
            }

            locations.forEach((loc) => {
                const locValue = `${loc.name}, ${loc.countryCode}`;
                const option = document.createElement("option");
                option.value = locValue;
                option.text = locValue;
                option.dataset.locationId = loc.id_json;

                if (selectedLocationParam && locValue === selectedLocationParam) {
                    option.selected = true;
                }

                select.appendChild(option);
            });

            // Якщо не передано параметр → вибираємо першу
            if (!selectedLocationParam) {
                select.selectedIndex = 0;
            }

            locationDisplay.textContent = select.value;

            // --- Запускаємо отримання погоди одразу ---
            const initialSelectedOption = select.options[select.selectedIndex];
            const initialLocationId = initialSelectedOption.dataset.locationId;
            fetchWeatherAndUpdate(initialLocationId);

            // --- Оновлення при виборі ---
            select.addEventListener('change', () => {
                locationDisplay.textContent = select.value;
                const selectedOption = select.options[select.selectedIndex];
                const locationId = selectedOption.dataset.locationId;
                fetchWeatherAndUpdate(locationId);
            });
        });
});

function fetchWeatherAndUpdate(locationId) {
    const apiKey = "80a1073bb7f6eeb206b6511bed52044a";
    fetch(`https://api.openweathermap.org/data/2.5/weather?id=${locationId}&units=metric&appid=${apiKey}`)
        .then(res => res.json())
        .then(data => updateWeatherCard(data))
        .catch(err => console.error("Error fetching weather:", err));
}

function updateWeatherCard(weatherData) {
    currentWeatherData = weatherData; // зберігаємо дані

    const tempValue = document.querySelector('.temp-value');
    const condition = document.querySelector('.condition');
    const feelsLike = document.querySelector('.feels-like');
    const date = document.querySelector('.date');
    const iconContainer = document.querySelector('.weather-icon');

    // Оновлення текстових даних
    if (tempValue) tempValue.textContent = `${Math.round(weatherData.main.temp)}°C`;
    if (condition) condition.textContent = capitalize(weatherData.weather[0].description);
    if (feelsLike) feelsLike.textContent = `Feels like ${Math.round(weatherData.main.feels_like)}°C`;

    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    const localDate = new Date(weatherData.dt * 1000);
    if (date) date.textContent = localDate.toLocaleDateString('en-US', options);

    // --- Іконка погоди ---
    const weatherIconCode = weatherData.weather[0].icon;
    const iconUrl = `https://openweathermap.org/img/wn/${weatherIconCode}@2x.png`;

    // Очищаємо попереднє (якщо є)
    if (iconContainer) {
        iconContainer.innerHTML = ''; // видаляємо старе
        const img = document.createElement('img');
        img.src = iconUrl;
        img.alt = weatherData.weather[0].description;
        img.style.width = '100%';
        iconContainer.appendChild(img);
    }

    // --- Деталі ---
    const detailItems = document.querySelectorAll('.weather-details .detail-item');

    if (detailItems.length >= 4) {
        const highLow = detailItems[0].querySelector('.value');
        const precipitation = detailItems[1].querySelector('.value');
        const humidity = detailItems[2].querySelector('.value');
        const wind = detailItems[3].querySelector('.value');

        if (highLow) highLow.textContent = `${Math.round(weatherData.main.temp_max)}° / ${Math.round(weatherData.main.temp_min)}°`;
        if (precipitation) precipitation.textContent = `${weatherData.clouds.all}%`;
        if (humidity) humidity.textContent = `${weatherData.main.humidity}%`;
        if (wind) wind.textContent = `${Math.round(weatherData.wind.speed * 3.6)} km/h`;
    } else {
        console.error("Not enough detail-item elements found!");
    }

    const infoCards = document.querySelectorAll('.additional-info .info-card');

    if (infoCards.length >= 3) {
        // Wind
        const windValue = infoCards[0].querySelector('.value');
        const windDesc = infoCards[0].querySelector('.description');
        if (windValue) windValue.textContent = `${Math.round(weatherData.wind.speed * 3.6)} km/h`;
        if (windDesc) windDesc.textContent = windDirection(weatherData.wind.deg);

        // Humidity
        const humidityValue = infoCards[1].querySelector('.value');
        const humidityDesc = infoCards[1].querySelector('.description');
        if (humidityValue) humidityValue.textContent = `${weatherData.main.humidity}%`;
        if (humidityDesc) humidityDesc.textContent = humidityLevelDescription(weatherData.main.humidity);

        // Pressure
        const pressureValue = infoCards[2].querySelector('.value');
        const pressureDesc = infoCards[2].querySelector('.description');
        if (pressureValue) pressureValue.textContent = `${weatherData.main.pressure} hPa`;
        if (pressureDesc) pressureDesc.textContent = pressureLevelDescription(weatherData.main.pressure);
    }
}

function capitalize(text) {
    return text.charAt(0).toUpperCase() + text.slice(1);
}

function windDirection(deg) {
    const directions = ['North', 'North-East', 'East', 'South-East', 'South', 'South-West', 'West', 'North-West'];
    const index = Math.round(deg / 45) % 8;
    return directions[index];
}

function humidityLevelDescription(humidity) {
    if (humidity < 30) return 'Low';
    if (humidity < 60) return 'Moderate';
    return 'High';
}

function pressureLevelDescription(pressure) {
    if (pressure < 1000) return 'Low';
    if (pressure <= 1020) return 'Normal';
    return 'High';
}
function loadUsernameFromTokenTable() {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    const payload = parseJwt(token);
    if (payload && payload.sub) {
        const usernameElement = document.querySelector('.user-name');
        if (usernameElement) {
            return payload.sub;
        }
    }
}

function changeLocation(selectedValue) {
    window.location.href = `index?location=${encodeURIComponent(selectedValue)}`;
}