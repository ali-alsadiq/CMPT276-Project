const passwordInput = document.getElementById('password');
const strengthBar = document.getElementById('password-strength-bar');
const strengthText = document.getElementById('password-strength-text');

passwordInput.addEventListener('input', function() {
    if (passwordInput.value.length > 0) {
        strengthBar.style.width = '75%';
        strengthBar.className = 'progress-bar bg-success';
        strengthText.textContent = 'Good';
        strengthText.className = 'text-success mt-1 d-block';
    } 
    
    else {
        strengthBar.style.width = '25%';
        strengthBar.className = 'progress-bar bg-danger';
        strengthText.textContent = 'Weak';
        strengthText.className = 'text-danger mt-1 d-block';
    }
});
