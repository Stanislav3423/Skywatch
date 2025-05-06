document.addEventListener('DOMContentLoaded', async () => {
    checkAuthentication();
    loadUsernameFromToken();

    const logoutButtons = document.querySelectorAll('.logout-btn');

    logoutButtons.forEach(button => {
        button.addEventListener('click', async (event) => {
            event.preventDefault();
            await logout();
        });
    });
});

async function logout() {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    const userRole = await getUserRole(token);

    if (userRole === 'GUEST') {
        try {
            await fetch('/auth/logout-guest', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                }
            });
            console.log('Guest deleted from database');
        } catch (error) {
            console.error('Error deleting guest:', error);
        }
    }

    localStorage.removeItem('jwt');
    window.location.href = 'signin';
}

async function getUserRole(token) {
    try {
        const response = await fetch('/auth/role', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            credentials: 'include' // Важливо для передачі кукі
        });

        console.log('Role response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const role = await response.text();
        console.log('Received role:', role);
        return role;

    } catch (error) {
        console.error('Error fetching user role:', error);
        return null;
    }
}

function checkAuthentication() {
    const token = localStorage.getItem('jwt');
    if (!token) {
        window.location.href = 'signin';
    } else {
        // При завантаженні перевіряємо, чи це Guest
        getUserRole(token).then(role => {
            isGuestSession = role === 'GUEST';
            if (isGuestSession) {
                sessionStorage.setItem('isGuest', 'true');
            }

            if (role === 'ADMIN') {
                const usersLink = document.querySelector('.nav-users');
                if (usersLink) {
                    usersLink.style.display = 'flex';
                    const editForecastBtn = document.getElementById('editForecastBtn');
                    if (editForecastBtn) {
                        editForecastBtn.style.display = 'inline-block';
                    }
                }
            }
            else if (role === 'GUEST') {
                const reportsLink = document.querySelector('.nav-reports');
                if (reportsLink) {
                    reportsLink.style.display = 'none';
                }
                const analyticsLink = document.querySelector('.nav-analytics');
                if (analyticsLink) {
                    analyticsLink.style.display = 'none';
                }
                const triggersLink = document.querySelector('.nav-alerts');
                if (triggersLink) {
                    triggersLink.style.display = 'none';
                }
            }
        });
    }
}

function loadUsernameFromToken() {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    const payload = parseJwt(token);
    //alert(payload.sub); // тепер правильно показує

    if (payload && payload.sub) {
        const usernameElement = document.querySelector('.user-name');
        if (usernameElement) {
            usernameElement.textContent = payload.sub;
        }

        const userAvatar = document.querySelector('.user-avatar');
        if (userAvatar && payload.sub.length > 0) {
            userAvatar.textContent = payload.sub[0].toUpperCase();
        }
    }
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error('Invalid token', e);
        return null;
    }
}
