const username = loadUsernameFromTokenTable();
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

document.addEventListener('DOMContentLoaded', () => {
    const triggersContainer = document.getElementById('triggersContainer');

    fetch(`/triggers/all-for-user/${username}`)
        .then(response => response.json())
        .then(triggers => {
            triggers.forEach(trigger => {
                const triggerDiv = document.createElement('div');
                triggerDiv.classList.add('forecast', 'trigger-forecast');

                triggerDiv.innerHTML = `
                    <h2>Trigger: ${trigger.name}</h2>
                    <p class="trigger-condition">Condition: ${trigger.condition}</p>
                    <p class="trigger-location">Location: ${trigger.location} (${trigger.countryCode})</p>
                    <div class="settings-tabs">
                        <button class="tab-item active">Daily</button>
                        <button class="tab-item">Hourly</button>
                    </div>
                    <div class="forecast-grid daily-grid"></div>
                    <div class="forecast-grid hourly-grid" style="display:none;"></div>
                `;

                triggersContainer.appendChild(triggerDiv);

                const dailyGrid = triggerDiv.querySelector('.daily-grid');
                const hourlyGrid = triggerDiv.querySelector('.hourly-grid');

                fetch(`/triggers/${trigger.id}/daily-forecasts`)
                    .then(resp => resp.json())
                    .then(dailyForecasts => {
                        dailyForecasts.forEach(forecast => {
                            const forecastItem = buildForecastItem(forecast);
                            dailyGrid.appendChild(forecastItem);
                        });
                    });

                fetch(`/triggers/${trigger.id}/hourly-forecasts`)
                    .then(resp => resp.json())
                    .then(hourlyForecasts => {
                        hourlyForecasts.forEach(forecast => {
                            const forecastItem = buildForecastItem(forecast);
                            hourlyGrid.appendChild(forecastItem);
                        });
                    });

                const tabs = triggerDiv.querySelectorAll('.tab-item');
                tabs.forEach(tab => {
                    tab.addEventListener('click', () => {
                        tabs.forEach(t => t.classList.remove('active'));
                        tab.classList.add('active');
                        if (tab.textContent === 'Daily') {
                            dailyGrid.style.display = '';
                            hourlyGrid.style.display = 'none';
                        } else {
                            dailyGrid.style.display = 'none';
                            hourlyGrid.style.display = '';
                        }
                    });
                });
            });
        });

    function buildForecastItem(forecast) {
        const item = document.createElement('div');
        item.classList.add('forecast-item');

        const dateStr = forecast.date || forecast.datetime || '';
        const dayStr = new Date(dateStr).toLocaleDateString('en-US', { weekday: 'long' });

        item.innerHTML = `
            <div class="day-info">
                <div class="day">${dayStr}</div>
                <div class="date">${dateStr}</div>
            </div>
            <!--<div class="forecast-icon">☀️</div>-->
            <img src="https://openweathermap.org/img/wn/${forecast.icon}@2x.png" alt="${forecast.condition}" class="forecast-icon" />
            <div class="condition">${forecast.condition || ''}</div>
            <div class="detail-item">
                <div class="label">Temp</div>
                <div class="value">${forecast.temperature}°C</div>
            </div>
            <div class="detail-item">
                <div class="label">Humidity</div>
                <div class="value">${forecast.humidity}%</div>
            </div>
            <div class="detail-item">
                <div class="label">Precipitation</div>
                <div class="value">${forecast.precipitation_amount}mm</div>
            </div>
        `;
        return item;
    }
});