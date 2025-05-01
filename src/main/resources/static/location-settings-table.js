function loadUserLocations(username) {
    fetch(`/locations/user/${username}`)
        .then(response => response.json())
        .then(data => {
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

            lucide.createIcons(); // оновити іконки після вставки
        })
        .catch(error => console.error('Error loading locations:', error));
}

function openLocationSearch() {
    const query = prompt("Enter location name to search:");
    if (!query) return;

    fetch(`/locations/search?query=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(locations => {
            if (locations.length === 0) {
                alert("No locations found");
                return;
            }

            const selected = prompt(
                "Select location by number:\n" +
                locations.map((l, i) => `${i+1}. ${l.name}, ${l.countryCode}`).join("\n")
            );

            const index = parseInt(selected) - 1;
            if (index >= 0 && index < locations.length) {
                addLocationToUser(locations[index].id);
            } else {
                alert("Invalid selection");
            }
        })
        .catch(err => console.error('Search error:', err));
}

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

document.querySelector('.add-btn').addEventListener('click', openLocationSearch);

const username = loadUsernameFromTokenTable();
if (username) {
    loadUserLocations(username);
} else {
    console.warn('No username found in token');
}