const locationModal = document.getElementById('locationModal');
const closeLocationModalBtn = document.getElementById('closeLocationModalBtn');
const select = document.getElementById('location-select');
document.getElementById('location-select').addEventListener('click', (e) => {
    locationModal.classList.add('active');
});

// Обробник пошуку при натисканні кнопки Search
document.getElementById('searchLocationBtn').addEventListener('click', () => {
    const query = document.getElementById('locationSearchInput').value.trim();
    const locationResults = document.getElementById('locationResults');

    if (!query) {
        locationResults.innerHTML = "<p>Please enter a location name</p>";
        return;
    }

    locationResults.innerHTML = "<p>Searching...</p>";

    fetch(`/locations/search?query=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(locations => {
            if (!locations.length) {
                locationResults.innerHTML = "<p>No locations found</p>";
                return;
            }

            locationResults.innerHTML = locations.map(l =>
                `<div class="location-item" data-id="${l.id}" data-name="${l.name}" data-country="${l.countryCode}">
                    <span class="name">${l.name}</span>
                    <span class="country">(${l.countryCode})</span>
                    <button class="add-location-btn">Add</button>
                 </div>`
            ).join('');
        })
        .catch(err => {
            console.error('Search error:', err);
            locationResults.innerHTML = "<p>Error loading locations</p>";
        });
});

// Обробник кліку на кнопках "Add" всередині результатів
document.getElementById('locationResults').addEventListener('click', (event) => {
    if (event.target.classList.contains('add-location-btn')) {
        const locationItem = event.target.closest('.location-item');
        const locationId = locationItem.getAttribute('data-id');
        const locationName = locationItem.getAttribute('data-name');
        const country = locationItem.getAttribute('data-country');

        // Оновити текст select
        select.options[0].text = `${locationName} (${country})`;
        select.options[0].value = locationId;

        // Закрити модальне вікно
        locationModal.classList.remove('active');
    }
});

// Закриття модального вікна
closeLocationModalBtn.addEventListener('click', () => {
    locationModal.classList.remove('active');
});

// Закриття при кліку поза модальним вікном
window.addEventListener('click', (event) => {
    if (event.target === locationModal) {
        locationModal.classList.remove('active');
    }
});

document.querySelector('.apply-filters').addEventListener('click', async () => {
    const period = document.getElementById('range-select').value;
    const locationId = select.options[0].value;
    const startDateTime = document.getElementById('start-datetime').value;
    const endDateTime = document.getElementById('end-datetime').value;

    // ----------------- TEMPERATURE запит -----------------
    const url = `/analytics/temperature?locationId=${locationId}&startDate=${encodeURIComponent(startDateTime)}&endDate=${encodeURIComponent(endDateTime)}&period=${period}`;
    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error('HTTP error ' + response.status);

        const data = await response.json();
        console.log(data);

        // Формуємо labels і data
        const labels = data.map(item => {
            const date = new Date(item.timestamp);
            if (period === 'hour') {
                return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            } else if (period === 'day') {
                return date.toLocaleDateString();
            } else if (period === 'month') {
                return date.toLocaleString('default', { month: 'short', year: 'numeric' });
            }
        });

        const temps = data.map(item => item.avgTemp);

        // Оновлюємо графік
        temperatureChart.data.labels = labels;
        temperatureChart.data.datasets[0].data = temps;
        temperatureChart.update();

    } catch (err) {
        console.error(err);
        alert('Error: ' + 'Invalid date');
    }

    // ----------------- WIND запит -----------------
    const windUrl = `/analytics/wind?locationId=${locationId}&startDate=${encodeURIComponent(startDateTime)}&endDate=${encodeURIComponent(endDateTime)}`;

    try {
        const response = await fetch(windUrl);
        if (!response.ok) throw new Error('HTTP error ' + response.status);

        const windData = await response.json();
        console.log('windData', windData);

        const windSpeeds = aggregateWindData(windData);

        windChart.data.datasets[0].data = windSpeeds;
        windChart.update();
    } catch (err) {
        console.error(err);
        alert('Wind error: Invalid date');
    }

    // ----------------- Presure запит -----------------

    const pressureUrl = `/analytics/pressure?locationId=${locationId}&startDate=${encodeURIComponent(startDateTime)}&endDate=${encodeURIComponent(endDateTime)}&period=${period}`;

    try {
        const response = await fetch(pressureUrl);
        if (!response.ok) throw new Error('HTTP error ' + response.status);

        const data = await response.json();
        console.log(data);

        // Формуємо labels через timestamp
        const labels = data.map(item => {
            const date = new Date(item.timestamp);
            if (period === 'hour') {
                return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            } else if (period === 'day') {
                return date.toLocaleDateString();
            } else if (period === 'month') {
                return date.toLocaleString('default', { month: 'short', year: 'numeric' });
            }
        });

        const pressures = data.map(item => item.avgPressure);

        pressureChart.data.labels = labels;
        pressureChart.data.datasets[0].data = pressures;
        pressureChart.update();

    } catch (err) {
        console.error(err);
        alert('Error: Invalid date');
    }

    // ----------------- Humidity запит -----------------
    const humidityUrl = `/analytics/humidity?locationId=${locationId}&startDate=${encodeURIComponent(startDateTime)}&endDate=${encodeURIComponent(endDateTime)}&period=${period}`;

    try {
        const response = await fetch(humidityUrl);
        if (!response.ok) throw new Error('HTTP error ' + response.status);

        const data = await response.json();
        console.log('humidityData', data);

        const labels = data.map(item => {
            const date = new Date(item.timestamp);
            if (period === 'hour') {
                return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            } else if (period === 'day') {
                return date.toLocaleDateString();
            } else if (period === 'month') {
                return date.toLocaleString('default', { month: 'short', year: 'numeric' });
            }
        });

        const humidities = data.map(item => item.avgHum);

        humidityChart.data.labels = labels;
        humidityChart.data.datasets[0].data = humidities;
        humidityChart.update();

    } catch (err) {
        console.error(err);
        alert('Humidity error: Invalid date');
    }

    // ----------------- Summary запит -----------------
    const summaryUrl = `/analytics/summary?locationId=${locationId}&startDate=${encodeURIComponent(startDateTime)}&endDate=${encodeURIComponent(endDateTime)}`;

    try {
        const response = await fetch(summaryUrl);
        if (!response.ok) throw new Error('HTTP error ' + response.status);

        const summaryData = await response.json();
        console.log('summaryData', summaryData);

        // Знаходимо елементи і оновлюємо значення
        document.querySelector('.stat-card:nth-child(1) .stat-value').textContent = summaryData.avgTemp.toFixed(1) + '°C';
        document.querySelector('.stat-card:nth-child(2) .stat-value').textContent = summaryData.avgWindSpeed.toFixed(1) + ' km/h';
        document.querySelector('.stat-card:nth-child(3) .stat-value').textContent = summaryData.avgHumidity.toFixed(1) + '%';
        document.querySelector('.stat-card:nth-child(4) .stat-value').textContent = summaryData.avgPressure.toFixed(1) + ' hPa';

    } catch (err) {
        console.error(err);
        alert('Summary error: ' + err.message);
    }

});
//--------------------------------------------------Temperature chart--------------------------------------------------
const temperatureCtx = document.getElementById('temperatureChart').getContext('2d');

const temperatureChart = new Chart(temperatureCtx, {
    type: 'line',
    data: {
        labels: [],
        datasets: [{
            label: 'Temperature (°C)',
            data: [],
            borderColor: '#51ab07',
            tension: 0.4
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false
    }
});

//--------------------------------------------------Wind chart--------------------------------------------------
function getDirection(degree) {
    const dirs = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
    const index = Math.round(degree / 45) % 8;
    return dirs[index];
}

function aggregateWindData(rawData) {
    const directions = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
    const dirSpeeds = {};

    // Ініціалізуємо всі напрямки значенням 0
    directions.forEach(dir => dirSpeeds[dir] = []);

    // Розподіляємо швидкості за напрямками
    rawData.forEach(item => {
        const dir = getDirection(item.deg);
        dirSpeeds[dir].push(item.avgSpeed);
    });

    // Обчислюємо середнє значення для кожного напрямку
    const avgSpeeds = directions.map(dir => {
        const speeds = dirSpeeds[dir];
        if (speeds.length === 0) return 0;
        return speeds.reduce((a, b) => a + b, 0) / speeds.length;
    });

    return avgSpeeds;
}

const windCtx = document.getElementById('windChart').getContext('2d');
const windChart = new Chart(windCtx, {
    type: 'radar',
    data: {
        labels: ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'],
        datasets: [{
            label: 'Wind Speed (km/h)',
            data: [0, 0, 0, 0, 0, 0, 0, 0], // спочатку пусто
            backgroundColor: 'rgba(37, 99, 235, 0.2)',
            borderColor: '#2563eb'
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false
    }
});

//--------------------------------------------------Pressure chart--------------------------------------------------

const pressureCtx = document.getElementById('pressureChart').getContext('2d');
const pressureChart = new Chart(pressureCtx, {
    type: 'line',
    data: {
        labels: [], // динамічно
        datasets: [{
            label: 'Pressure (hPa)',
            data: [],
            borderColor: '#ef4444', // 🔴 червоний
            tension: 0.4
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            annotation: {
                annotations: {
                    normalPressureLine: {
                        type: 'line',
                        yMin: 1013.25,
                        yMax: 1013.25,
                        borderColor: 'black',
                        borderWidth: 1.5,
                        borderDash: [6, 6],
                        label: {
                            content: 'Normal Pressure',
                            enabled: true,
                            position: 'end',
                            backgroundColor: 'rgba(39,206,170,0.7)',
                            color: '#fff',
                            padding: 4
                        }
                    }
                }
            }
        }
    }
});

//--------------------------------------------------Humidity chart--------------------------------------------------

const humidityCtx = document.getElementById('humidityChart').getContext('2d');
const humidityChart = new Chart(humidityCtx, {
    type: 'bar',
    data: {
        labels: [], // буде заповнюватися динамічно
        datasets: [{
            label: 'Humidity (%)',
            data: [],
            backgroundColor: 'rgba(241,223,45,0.6)',
            borderColor: '#d1d53a',
            borderWidth: 1
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            y: {
                beginAtZero: true,
                max: 100
            }
        }
    }
});

// Button
const reportModal = document.getElementById('reportModal');
const closeReportModalBtn = document.getElementById('closeReportModalBtn');
document.getElementById('generateReportBtn').addEventListener('click', (e) => {
    reportModal.classList.add('active');
});

document.getElementById('createReportButton').addEventListener('click', (event) => {
    if (event.target.classList.contains('modal-search-btn')) {
        const locationId = select.value;
        const location = select.options[select.selectedIndex].text;

        const startDateTime = document.getElementById('start-datetime').value;
        const endDateTime = document.getElementById('end-datetime').value;

        fetch(`/analytics/summary?locationId=${locationId}&startDate=${startDateTime}&endDate=${endDateTime}`)
            .then(response => response.json())
            .then(data => {
                const reportData = {
                    name: document.getElementById('reportNameText').value,
                    location: location,
                    startDate: startDateTime,
                    endDate: endDateTime,
                    avgTemp: data.avgTemp,
                    avgHumidity: data.avgHumidity,
                    maxTemp: data.maxTemp,
                    minTemp: data.minTemp,
                    avgCloudiness: data.avgCloudiness,
                    avgPressure: data.avgPressure,
                    avgWindSpeed: data.avgWindSpeed
                };

                const token = localStorage.getItem('jwt');

                fetch('/reports/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(reportData),
                })
                    .then(response => {
                        if (response.ok) {
                            alert('Report successfully created!');
                        } else {
                            alert('Failed to create report.');
                        }
                    })
                    .catch(error => {
                        console.error('Error creating report:', error);
                        alert('Error creating report.');
                    });
            })
            .catch(error => {
                console.error('Error fetching summary:', error);
                alert('Error fetching summary.');
            });

        // Закрити модальне вікно
        reportModal.classList.remove('active');
    }
});

closeReportModalBtn.addEventListener('click', () => {
    reportModal.classList.remove('active');
});

window.addEventListener('click', (event) => {
    if (event.target === reportModal) {
        reportModal.classList.remove('active');
    }
});