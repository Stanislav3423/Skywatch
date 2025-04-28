/*document.addEventListener('DOMContentLoaded', checkAuthentication);
window.addEventListener('beforeunload', function (e) {
    //logoutGuest();
});

//document.getElementById('logoutButton').addEventListener('click', logout);

document.addEventListener('DOMContentLoaded', () => {
    checkAuthentication();

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

async function logoutGuest() {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
        await fetch('/auth/logout-guest', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });
    } catch (error) {
        console.error('Guest logout failed', error);
    } finally {
        alert('guest deleted')
        localStorage.removeItem('jwt');
    }
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
        });
    }
}*/

/*
document.addEventListener('DOMContentLoaded', checkAuthentication);
window.addEventListener('beforeunload', handlePageUnload);

document.getElementById('logoutButton').addEventListener('click', handleLogout);

// Глобальна змінна для відстеження Guest сесії
let isGuestSession = false;

async function handleLogout() {
    await performLogout();
    window.location.href = 'signin';
}

async function handlePageUnload(event) {
    // Для браузерів, що підтримують sendBeacon
    if (navigator.sendBeacon && isGuestSession) {
        const token = localStorage.getItem('jwt');
        if (token) {
            const headers = new Headers({
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            });
            navigator.sendBeacon('/auth/logout-guest', { headers });
        }
    }
    // Стандартний спосіб для інших випадків
    await performLogout();

    // Дозволяємо сторінці перезавантажитись
    delete event['returnValue'];
}

async function performLogout() {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
        const userRole = await getUserRole(token);
        alert(userRole)
        isGuestSession = userRole === 'GUEST';

        if (isGuestSession) {
            await deleteGuestAccount(token);
            console.log('Guest account deleted');
        } else {
            await standardLogout(token);
            console.log('Regular user logged out');
        }
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        localStorage.removeItem('jwt');
        sessionStorage.removeItem('isGuest');
    }
}

async function deleteGuestAccount(token) {
    try {
        // Використовуємо fetch з keepalive для надійності
        const response = await fetch('/auth/logout-guest', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            keepalive: true // Важливо для beforeunload
        });

        if (!response.ok) {
            throw new Error('Failed to delete guest account');
        }
    } catch (error) {
        console.error('Error deleting guest:', error);
        // Спробуємо ще раз через sendBeacon
        if (navigator.sendBeacon) {
            const headers = new Headers({
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            });
            navigator.sendBeacon('/auth/logout-guest', { headers });
        }
    }
}

async function standardLogout(token) {
    try {
        await fetch('/auth/logout', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });
    } catch (error) {
        console.error('Logout failed:', error);
    }
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
        });
    }
}*/
