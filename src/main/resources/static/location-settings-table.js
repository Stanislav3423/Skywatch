const username = loadUsernameFromTokenTable();
let userLocations = [];

if (username) {
    loadUserLocations(username);
    loadUserTriggers(username);
    account_det = document.getElementById("acc-details-p")
    account_det.innerHTML = `<p>Username: ${username}</p>`;

} else {
    console.warn('No username found in token');
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

const tabs = document.querySelectorAll('.tab-item');
const tabContents = document.querySelectorAll('.tab-content');

tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        tabs.forEach(t => t.classList.remove('active'));
        tabContents.forEach(c => c.classList.remove('active'));

        tab.classList.add('active');
        const tabId = tab.dataset.tab;
        const content = document.getElementById(tabId);
        if (content) {
            content.classList.add('active');
        } else {
            console.error(`Element with id="${tabId}" not found!`);
        }
    });
});

//----------------------------------------Locations----------------------------------------

// Отримуємо елементи
const openLocationModalBtn = document.getElementById('openLocationModalBtn');
const locationModal = document.getElementById('locationModal');
const closeLocationModalBtn = document.getElementById('closeLocationModalBtn');
const searchLocationBtn = document.getElementById('searchLocationBtn');
const locationSearchInput = document.getElementById('locationSearchInput');
const locationResults = document.getElementById('locationResults');

function loadUserLocations(username) {
    fetch(`/locations/user/${username}`)
        .then(response => response.json())
        .then(data => {
            userLocations = data;

            const tbody = document.querySelector(".settings-table tbody");
            tbody.innerHTML = '';

            data.forEach(location => {
                const tr = document.createElement("tr");

                tr.innerHTML = `
                    <td>${location.name}, ${location.countryCode}</td>
                    <td>${location.lat}</td>
                    <td>${location.lon}</td>
                    <td class="actions">
                        <button class="action-btn delete" data-id="${location.id}">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });

            lucide.createIcons();
        })
        .catch(error => console.error('Error loading locations:', error));
}

// Відкриття модального вікна
openLocationModalBtn.addEventListener('click', () => {
    locationModal.classList.add('active');
});

// Закриття модального вікна
closeLocationModalBtn.addEventListener('click', () => {
    locationModal.classList.remove('active');
});

// Закриття модального при кліку за межами контенту
window.addEventListener('click', (event) => {
    if (event.target === locationModal) {
        locationModal.classList.remove('active');
    }
});

// Пошук локації
searchLocationBtn.addEventListener('click', () => {
    const query = locationSearchInput.value.trim();
    if (!query) return;

    // Очистити попередні результати
    locationResults.innerHTML = "<p>Searching...</p>";

    fetch(`/locations/search?query=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(locations => {
            if (!locations.length) {
                locationResults.innerHTML = "<p>No locations found</p>";
                return;
            }

            // Створити кнопки для результатів
            locationResults.innerHTML = locations.map((l, i) =>
                `<div class="location-item" data-id="${l.id}">
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
locationResults.addEventListener('click', (event) => {
    if (event.target.classList.contains('add-location-btn')) {
        const locationItem = event.target.closest('.location-item');
        const locationId = locationItem.getAttribute('data-id');
        addLocationToUser(locationId);
    }
});

function addLocationToUser(locationId) {
    const username = loadUsernameFromTokenTable();
    fetch(`/locations/add-to-user?username=${encodeURIComponent(username)}&locationId=${locationId}`, {
        method: 'POST'
    })
        .then(res => {
            if (res.ok) {
                alert("Location added!");
                loadUserLocations(username);
            } else {
                alert("Failed to add location");
            }
        })
        .catch(err => console.error('Add error:', err));
}

document.querySelector(".settings-table").addEventListener("click", (e) => {
    if (e.target.closest('.delete')) {
        const locationId = e.target.closest('.delete').dataset.id;
        if (confirm("Delete this location?")) {
            deleteUserLocation(locationId);
        }
    }
});

function deleteUserLocation(locationId) {
    const username = loadUsernameFromTokenTable();
    fetch(`/locations/delete-from-user?username=${encodeURIComponent(username)}&locationId=${locationId}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) {
                alert("Location deleted");
                loadUserLocations(username);
            } else {
                alert("Failed to delete");
            }
        })
        .catch(err => console.error('Delete error:', err));
}

//----------------------------------------Triggers----------------------------------------
function loadUserTriggers(username) {
    fetch(`/triggers/all-for-user/${username}`)
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector("#triggersTable tbody");
            tbody.innerHTML = '';

            data.forEach(trigger => {
                const tr = document.createElement("tr");

                tr.innerHTML = `
                    <td>${trigger.name}</td>
                    <td>${trigger.condition}</td>
                    <td>${trigger.location}, ${trigger.countryCode}</td>
                    <td class="actions">
                        <button class="action-btn delete" data-id="${trigger.id}">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </td>
                `;

                tbody.appendChild(tr);
            });

            lucide.createIcons();
        })
        .catch(error => console.error('Error loading triggers:', error));
}

document.querySelector("#triggers").addEventListener("click", (e) => {
    if (e.target.closest('.delete')) {
        const triggerId = e.target.closest('.delete').dataset.id;
        if (confirm("Delete this trigger?")) {
            deleteUserTrigger(triggerId);
        }
    }
});

function deleteUserTrigger(triggerId) {
    const username = loadUsernameFromTokenTable(); // як ти робиш у локаціях
    fetch(`/triggers/${triggerId}?username=${encodeURIComponent(username)}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) {
                alert("Trigger deleted");
                loadUserTriggers(username); // перезавантажити список
            } else {
                alert("Failed to delete trigger");
            }
        })
        .catch(err => console.error('Delete trigger error:', err));
}

const openTriggerModalBtn = document.getElementById('openTriggerModalBtn');
const triggerModal = document.getElementById('triggerModal');
const closeTriggerModalBtn = document.getElementById('closeTriggerModalBtn');
const triggerLocationSelect = document.getElementById('triggerLocation');

openTriggerModalBtn.addEventListener('click', () => {
    triggerLocationSelect.innerHTML = userLocations.map(loc =>
        `<option value="${loc.id}">${loc.name}, ${loc.countryCode}</option>`
    ).join('');

    triggerModal.classList.add('active');
});

closeTriggerModalBtn.addEventListener('click', () => {
    triggerModal.classList.remove('active');
});

window.addEventListener('click', (e) => {
    if (e.target === triggerModal) {
        triggerModal.classList.remove('active');
    }
});

const triggerForm = document.getElementById('triggerForm');

triggerForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const username = loadUsernameFromTokenTable();

    const triggerRequest = {
        name: document.getElementById('triggerName').value.trim(),
        parameter: document.getElementById('triggerParameter').value,
        operator: document.getElementById('triggerOperator').value,
        value: parseFloat(document.getElementById('triggerValue').value),
        locationId: parseInt(document.getElementById('triggerLocation').value),
        username: username
    };

    fetch('/triggers/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(triggerRequest)
    })
        .then(res => {
            if (res.ok) {
                alert('Trigger created!');
                triggerModal.classList.remove('active');
                loadUserTriggers(username); // якщо є функція завантаження
            } else {
                alert('Failed to create trigger');
            }
        })
        .catch(err => console.error('Create trigger error:', err));
});