let currentWeatherData = null;
let currentLocationId = null;
let currentTypeOfForecast = null;
//let url_parametr = "/standart";
let url_parametr = "";

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
    window.location.href = `dashboard?location=${encodeURIComponent(selectedValue)}`;
}

const btn7day = document.getElementById('btn-7day');
const btn12hour = document.getElementById('btn-12hour');
const forecastContainer = document.getElementById('forecast-container');


async function loadForecast(type, locationId) {
    currentTypeOfForecast = type;
    let url = `/dashboard/forecast/${type}${url_parametr}/${locationId}`;
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
            data-id="${item.id}"
            data-type="${type}"
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
            const id = itemEl.dataset.id;
            const type = itemEl.dataset.type;
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

            document.getElementById('editForecastBtn').dataset.id = id;
            document.getElementById('editForecastBtn').dataset.date = date;
            document.getElementById('editForecastBtn').dataset.temperature = temperature;
            document.getElementById('editForecastBtn').dataset.condition = condition;
            document.getElementById('editForecastBtn').dataset.pressure = pressure;
            document.getElementById('editForecastBtn').dataset.windDir = windDir;
            document.getElementById('editForecastBtn').dataset.windSpeed = windSpeed;
            document.getElementById('editForecastBtn').dataset.precipAmount = precipAmount;
            document.getElementById('editForecastBtn').dataset.precipProb = precipProb;

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

            const editBtn = document.getElementById('editForecastBtn');
            editBtn.dataset.id = id;
            editBtn.dataset.type = type;

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

checkAuthentication()

let editingForecastId = null;
document.getElementById("editForecastBtn").addEventListener("click", (e) => {
    const btn = e.currentTarget;
    const forecastId = btn.dataset.id;
    const date = btn.dataset.date;
    const temperature = btn.dataset.temperature;
    const pressure = btn.dataset.pressure;
    const windDir = btn.dataset.windDir;
    const windSpeed = btn.dataset.windSpeed;
    const precipAmount = btn.dataset.precipAmount;
    const precipProb = btn.dataset.precipProb;

    console.log('Редагуємо прогноз з ID:', forecastId);

    openEditModal(forecastId, date, temperature, pressure, windDir, windSpeed, precipAmount, precipProb);
});

function openEditModal(forecastId, date, temperature, pressure, windDir, windSpeed, precipAmount, precipProb) {
    editingForecastId = forecastId;
    document.querySelectorAll('.form-control').forEach(input => {
        input.style.borderColor = '#e2e8f0';
    });
    document.getElementById('edit_date').value = date;
    document.getElementById('edit_temperature').value = temperature;
    document.getElementById('edit_pressure').value = pressure;
    document.getElementById('edit_wind_dir').value = windDir;
    document.getElementById('edit_wind_speed').value = windSpeed;
    document.getElementById('edit_precipitation_amount').value = precipAmount;
    document.getElementById('edit_precipitation_probability').value = precipProb;
    document.getElementById('forecastEditModal').classList.add('active');
}

async function handleForecastEditSubmit(event) {
    event.preventDefault();

    // Отримуємо значення з форми
    const tempValue = document.getElementById('edit_temperature').value;
    const pressureValue = document.getElementById('edit_pressure').value;
    const windDirValue = document.getElementById('edit_wind_dir').value;
    const windSpeedValue = document.getElementById('edit_wind_speed').value;
    const precipAmountValue = document.getElementById('edit_precipitation_amount').value;
    const precipProbValue = document.getElementById('edit_precipitation_probability').value;

    // Валідація значень
    try {
        // Температура (-50 до +50 °C)
        if (isNaN(tempValue)) throw new Error('Temperature must be a number');
        const temperature = parseFloat(tempValue);
        if (temperature < -50 || temperature > 50) {
            throw new Error('Temperature must be between -50 and +50 °C');
        }

        // Тиск (800 до 1100 hPa)
        if (isNaN(pressureValue)) throw new Error('Pressure must be a number');
        const pressure = parseInt(pressureValue);
        if (pressure < 800 || pressure > 1100) {
            throw new Error('Pressure must be between 800 and 1100 hPa');
        }

        // Напрямок вітру (0-360 градусів)
        if (isNaN(windDirValue)) throw new Error('Wind direction must be a number');
        const windDeg = parseInt(windDirValue);
        if (windDeg < 0 || windDeg > 360) {
            throw new Error('Wind direction must be between 0 and 360 degrees');
        }

        // Швидкість вітру (0-100 m/s)
        if (isNaN(windSpeedValue)) throw new Error('Wind speed must be a number');
        const windSpeed = parseFloat(windSpeedValue);
        if (windSpeed < 0 || windSpeed > 100) {
            throw new Error('Wind speed must be between 0 and 100 m/s');
        }

        // Кількість опадів (0-500 mm)
        if (isNaN(precipAmountValue)) throw new Error('Precipitation must be a number');
        const precipitationAmount = parseFloat(precipAmountValue);
        if (precipitationAmount < 0 || precipitationAmount > 500) {
            throw new Error('Precipitation must be between 0 and 500 mm');
        }

        // Ймовірність опадів (0-100%)
        if (isNaN(precipProbValue)) throw new Error('Precipitation probability must be a number');
        const precipitationProbability = parseFloat(precipProbValue);
        if (precipitationProbability < 0 || precipitationProbability > 100) {
            throw new Error('Precipitation probability must be between 0 and 100%');
        }

        // Якщо валідація пройдена - формуємо об'єкт для відправки
        const forecastData = {
            id: editingForecastId,
            forecastType: document.getElementById("editForecastBtn").dataset.type,
            temperature,
            pressure,
            windDeg,
            windSpeed,
            precipitationAmount,
            precipitationProbability
        };

        const response = await fetch('dashboard/forecast/update', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwt')}`
            },
            body: JSON.stringify(forecastData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to update forecast');
        }

        closeForecastEditModal();
        loadForecast(currentTypeOfForecast, currentLocationId);
        alert('Forecast updated successfully');

    } catch (error) {
        console.error('Validation error:', error);
        alert('Validation error: ' + error.message);
        // Підсвічуємо поле з помилкою
        highlightErrorField(error.message);
    }
}

// Функція для підсвічування поля з помилкою
function highlightErrorField(errorMessage) {
    document.querySelectorAll('.form-control').forEach(input => {
        input.style.borderColor = '#e2e8f0';
    });

    // Визначаємо, яке поле має помилку
    if (errorMessage.includes('Temperature')) {
        document.getElementById('edit_temperature').style.borderColor = 'red';
    } else if (errorMessage.includes('Pressure')) {
        document.getElementById('edit_pressure').style.borderColor = 'red';
    } else if (errorMessage.includes('Wind direction')) {
        document.getElementById('edit_wind_dir').style.borderColor = 'red';
    } else if (errorMessage.includes('Wind speed')) {
        document.getElementById('edit_wind_speed').style.borderColor = 'red';
    } else if (errorMessage.includes('Precipitation must be')) {
        document.getElementById('edit_precipitation_amount').style.borderColor = 'red';
    } else if (errorMessage.includes('Precipitation probability')) {
        document.getElementById('edit_precipitation_probability').style.borderColor = 'red';
    }
}

function closeForecastEditModal() {
    document.getElementById('forecastEditModal').classList.remove('active');
}