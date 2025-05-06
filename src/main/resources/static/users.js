function loadUsers() {
    fetch(`/users/all`)
        .then(response => response.json())
        .then(data => {
            //userLocations = data;
            const tbody = document.querySelector(".settings-table tbody");
            tbody.innerHTML = '';

            data.forEach(user => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.role}</td>
                    <td>${user.enabled}</td>
                    <td class="actions">
                        <button class="action-btn delete" data-id="${user.id}" data-role="${user.role}">
                            <i data-lucide="trash-2"></i>
                        </button>
                        <button class="action-btn edit" data-id="${user.id}" data-role="${user.role}">
                            <i data-lucide="square-pen"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
            lucide.createIcons();
        })
        .catch(error => console.error('Error loading locations:', error));
}

document.querySelector("#users").addEventListener("click", (e) => {
    const deleteBtn = e.target.closest('.delete');
    if (deleteBtn) {
        const userId = deleteBtn.dataset.id;
        const userRole = deleteBtn.dataset.role;
        console.log(userRole)

        if (userRole === 'ADMIN') {
            alert("You cannot delete an ADMIN user.");
            return;
        }

        if (confirm("Delete this user?")) {
            deleteUser(userId);
        }
    }
});

function deleteUser(userId) {
    fetch(`/users/delete/${userId}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) {
                alert("User deleted");
                loadUsers();
            } else {
                alert("Failed to delete user");
            }
        })
        .catch(err => console.error('Delete user error:', err));
}

let editingUserId = null;
document.querySelector("#users").addEventListener("click", (e) => {
    const editBtn = e.target.closest('.edit');
    if (editBtn) {
        if (editBtn.dataset.role === 'ADMIN') {
            alert("You cannot edit an ADMIN user.");
            return;
        }
        const userId = editBtn.dataset.id;
        const row = editBtn.closest('tr');
        const username = row.querySelector('td:nth-child(2)').innerText;
        const role = row.querySelector('td:nth-child(3)').innerText;
        const enabled = row.querySelector('td:nth-child(4)').innerText === 'true';
        openEditModal(userId, username, role, enabled);
    }
});

function openEditModal(userId, username, role, enabled) {
    editingUserId = userId;

    document.getElementById('username').value = username;
    document.getElementById('role').value = role;
    document.getElementById('status').checked = enabled;
    document.getElementById('statusText').innerText = enabled ? 'Active' : 'Disabled';

    document.getElementById('userEditModal').classList.add('active');
}

function closeModal() {
    document.getElementById('userEditModal').classList.remove('active');
}

document.getElementById('status').addEventListener('change', (e) => {
    document.getElementById('statusText').innerText = e.target.checked ? 'Active' : 'Disabled';
});

function handleSubmit(event) {
    event.preventDefault();

    const role = document.getElementById('role').value;
    const enabled = document.getElementById('status').checked;

    const updatedUser = { role: role, enabled: enabled };

    fetch(`/users/update/${editingUserId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedUser)
    })
        .then(res => {
            if (res.ok) {
                alert('User updated successfully');
                closeModal();
                loadUsers();
            } else {
                alert('Failed to update user');
            }
        })
        .catch(err => console.error('Update user error:', err));
}

loadUsers();