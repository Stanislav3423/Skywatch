document.getElementById('signupForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });

    if (response.ok) {
        //alert('Registration successful! Please sign in.');
        window.location.href = 'signin';
    } else {
        const errorText = await response.text();
        alert('Registration failed: ' + errorText);
    }
});