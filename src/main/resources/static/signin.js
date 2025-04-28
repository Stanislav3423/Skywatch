document.getElementById('signinForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/auth/signin', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });

    if (response.ok) {
        const token = await response.text();
        localStorage.setItem('jwt', token);
        window.location.href = 'index';
        console.log(token)
    } else {
        alert('Invalid credentials!');
    }
});

document.getElementById('guestLogin').addEventListener('click', async function(e) {
    e.preventDefault();

    try {
        const response = await fetch('/auth/guest', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        if (response.ok) {
            const token = await response.text();
            localStorage.setItem('jwt', token);
            window.location.href = 'index';
        } else {
            alert('Failed to login as guest.');
        }
    } catch (error) {
        console.error('Error during guest login:', error);
        alert('Error during guest login.');
    }
});