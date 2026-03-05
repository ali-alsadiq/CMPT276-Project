const passwordInput = document.getElementById('password');
const strengthBar = document.getElementById('password-strength-bar');
const strengthText = document.getElementById('password-strength-text');

passwordInput.addEventListener('input', function() {
    const password = passwordInput.value;

    let score = calculatePasswordStrength(password);

    updatePasswordStrength(score);
});

function calculatePasswordStrength(password) {
    let score = 0;

    if (password.length === 0) {
        return score;
    }

    if (password.length > 0) {
        score++;
    }

    if (password.length >= 8) {
        score++;
    }

    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) {
        score++;
    }

    if (/[0-9]/.test(password)) {
        score++;
    }

    if (/[!@#$%^&*]/.test(password)) {
        score++;
    }

    return score;
}

function updatePasswordStrength(score) {
    if (score === 0) {
        strengthBar.style.width = '0%';
        strengthBar.className = 'progress-bar';
        strengthText.textContent = 'None';
        strengthText.className = 'text-muted mb-0 fw-bold';
    } 

    else if (score == 1) {
        strengthBar.style.width = '10%';
        strengthBar.className = 'progress-bar bg-danger';
        strengthText.textContent = 'Weak';
        strengthText.className = 'text-danger mb-0 fw-bold';
    }
    
    else if (score === 2) {
        strengthBar.style.width = '25%';
        strengthBar.className = 'progress-bar bg-danger';
        strengthText.textContent = 'Weak';
        strengthText.className = 'text-danger mb-0 fw-bold';
    } 
    
    else if (score === 3) {
        strengthBar.style.width = '50%';
        strengthBar.className = 'progress-bar bg-warning';
        strengthText.textContent = 'Fair';
        strengthText.className = 'text-warning mb-0 fw-bold';
    } 
    
    else if (score === 4) {
        strengthBar.style.width = '75%';
        strengthBar.className = 'progress-bar bg-success';
        strengthText.textContent = 'Good';
        strengthText.className = 'text-success mb-0 fw-bold';
    }
    
    else {
        strengthBar.style.width = '100%';
        strengthBar.className = 'progress-bar bg-success';
        strengthText.textContent = 'Strong';
        strengthText.className = 'text-success mb-0 fw-bold';
    }
}
