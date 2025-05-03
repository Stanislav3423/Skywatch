let currentWeatherData = null;
let currentLocationId = null;

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
                    currentLocationId = loc.id_json; // <-- ОНОВЛЕННЯ ЗМІННОЇ
                }

                select.appendChild(option);
            });

            // Якщо не передано параметр → вибираємо першу
            if (!selectedLocationParam) {
                select.selectedIndex = 0;
                const initialSelectedOption = select.options[0];
                currentLocationId = initialSelectedOption.dataset.locationId;
            }

            locationDisplay.textContent = select.value;

            // Після ініціалізації — завантаження погоди та прогнозу
            fetchWeatherAndUpdate(currentLocationId);
            loadForecast('7day', currentLocationId); // <-- ДОДАЄМО

            // --- Оновлення при виборі ---
            select.addEventListener('change', () => {
                locationDisplay.textContent = select.value;
                const selectedOption = select.options[select.selectedIndex];
                const locationId = selectedOption.dataset.locationId;
                changeLocation(select.value);
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

const btn7day = document.getElementById('btn-7day');
const btn12hour = document.getElementById('btn-12hour');
const forecastContainer = document.getElementById('forecast-container');


async function loadForecast(type, locationId) {
    let url = `/dashboard/forecast/${type}/${locationId}`;
    const response = await fetch(url);
    const data = await response.json();

    let title = type === '7day' ? '7-Day Forecast' : '12-Hour Forecast';

    let html = `
            <section class="forecast">
                <h2>${title}</h2>
                <div class="forecast-grid">
        `;

    data.forEach(item => {
        html += `
        <div class="forecast-item" 
            data-day="${item.dayOrHour}"
            data-date="${item.date}"
            data-icon="${item.icon}"
            data-temperature="${item.temperature}"
            data-pressure="${item.pressure}"
            data-wind-dir="${item.wind_dir}"
            data-wind-speed="${item.wind_speed}"
            data-precipitation-amount="${item.precipitation_amount}"
            data-precipitation-probability="${item.precipitation_probability}"
            data-condition="${item.condition}">
            
            <div class="day-info">
                <p class="day">${item.dayOrHour}</p>
                <p class="date">${item.date}</p>
            </div>
            <img src="https://openweathermap.org/img/wn/${item.icon}@2x.png" alt="${item.condition}" class="forecast-icon" />
            <div class="forecast-temp">
                <p class="temp">${item.temperature}°</p>
                <p class="condition">${item.condition}</p>
            </div>
        </div>
    `;
    });

    html += `</div></section>`;

    forecastContainer.innerHTML = html;
    lucide.createIcons();

    // Очистимо існуючі графіки
    if (window.tempChart instanceof Chart) window.tempChart.destroy();
    if (window.windChart instanceof Chart) window.windChart.destroy();
    if (window.precipChart instanceof Chart) window.precipChart.destroy();

// Масиви даних для графіків
    const labels = data.map(item => item.dayOrHour);
    const temperatures = data.map(item => parseFloat(item.temperature));
    const windSpeeds = data.map(item => parseFloat(item.wind_speed));
    const precipitations = data.map(item => parseFloat(item.precipitation_amount));

// Температурний графік
    const tempCtx = document.getElementById('temperatureChart').getContext('2d');
    window.tempChart = new Chart(tempCtx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Temperature (°C)',
                data: temperatures,
                borderColor: '#007bff',
                backgroundColor: 'rgba(0, 123, 255, 0.1)',
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: type === '7day' ? 'Day' : 'Hour' } },
                y: { title: { display: true, text: '°C' } }
            }
        }
    });

// Графік швидкості вітру
    const windCtx = document.getElementById('windChart').getContext('2d');
    window.windChart = new Chart(windCtx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Wind Speed (m/s)',
                data: windSpeeds,
                borderColor: '#28a745',
                backgroundColor: 'rgba(40, 167, 69, 0.1)',
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: type === '7day' ? 'Day' : 'Hour' } },
                y: { title: { display: true, text: 'm/s' } }
            }
        }
    });

// Графік опадів
    const precipCtx = document.getElementById('precipitationChart').getContext('2d');
    window.precipChart = new Chart(precipCtx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Precipitation (mm)',
                data: precipitations,
                backgroundColor: '#ffc107'
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: type === '7day' ? 'Day' : 'Hour' } },
                y: { title: { display: true, text: 'mm' } }
            }
        }
    });

    forecastContainer.querySelectorAll('.forecast-item').forEach(itemEl => {
        itemEl.addEventListener('click', () => {
            const day = itemEl.dataset.day;
            const date = itemEl.dataset.date;
            const icon = itemEl.dataset.icon;
            const temperature = itemEl.dataset.temperature;
            const pressure = itemEl.dataset.pressure;
            const windDir = itemEl.dataset['windDir'];
            const windSpeed = itemEl.dataset['windSpeed'];
            const precipAmount = itemEl.dataset['precipitationAmount'];
            const precipProb = itemEl.dataset['precipitationProbability'];
            const condition = itemEl.dataset.condition;

            // Заповнюємо модальне вікно
            document.getElementById('modalTitle').textContent = `${day} (${date})`;
            document.getElementById('modalIcon').src = `https://openweathermap.org/img/wn/${icon}@2x.png`;
            document.getElementById('modalIcon').alt = condition;
            document.getElementById('modalTemp').textContent = temperature;
            document.getElementById('modalCondition').textContent = condition;
            document.getElementById('modalPressure').textContent = pressure;
            document.getElementById('modalWindDir').textContent = windDir;
            document.getElementById('modalWindSpeed').textContent = windSpeed;
            document.getElementById('modalPrecipAmount').textContent = precipAmount;
            document.getElementById('modalPrecipProb').textContent = precipProb;

            // Показати модальне вікно (оновлений спосіб)
            const modal = document.getElementById('forecastModal');
            modal.classList.add('active');
            modal.style.display = 'flex'; // Додаємо для негайного відображення
        });
    });

}

btn7day.addEventListener('click', () => {
    btn7day.classList.add('active');
    btn12hour.classList.remove('active');
    if (currentLocationId) {
        loadForecast('7day', currentLocationId);
    }
});

btn12hour.addEventListener('click', () => {
    btn12hour.classList.add('active');
    btn7day.classList.remove('active');
    if (currentLocationId) {
        loadForecast('12hour', currentLocationId);
    }
});
/*
document.getElementById('closeModal').addEventListener('click', () => {
    document.getElementById('forecastModal').style.display = 'none';
});

window.addEventListener('click', (event) => {
    if (event.target === document.getElementById('forecastModal')) {
        document.getElementById('forecastModal').style.display = 'none';
    }
});*/
// Оновлена логіка закриття
document.getElementById('closeModal').addEventListener('click', () => {
    const modal = document.getElementById('forecastModal');
    modal.classList.remove('active');
    setTimeout(() => {
        modal.style.display = 'none';
    }, 300);
});

window.addEventListener('click', (event) => {
    if (event.target === document.getElementById('forecastModal')) {
        const modal = document.getElementById('forecastModal');
        modal.classList.remove('active');
        setTimeout(() => {
            modal.style.display = 'none';
        }, 300);
    }
});