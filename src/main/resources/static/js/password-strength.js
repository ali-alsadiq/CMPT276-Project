const passwordInput = document.getElementById('password');
const strengthBar = document.getElementById('password-strength-bar');

passwordInput.addEventListener('input', function() {
    if (passwordInput.value.length === 0) {
        strengthBar.style.width = '0%';
        strengthBar.className = 'progress-bar';
    } else if (passwordInput.value.length > 5) {
        strengthBar.style.width = '75%';
        strengthBar.className = 'progress-bar bg-success';
    } else {
        strengthBar.style.width = '25%';
        strengthBar.className = 'progress-bar bg-danger';
    }
});
