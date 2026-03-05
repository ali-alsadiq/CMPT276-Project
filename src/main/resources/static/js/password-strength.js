const passwordInput = document.getElementById('password');
const strengthBar = document.getElementById('password-strength-bar');
const strengthText = document.getElementById('password-strength-text');

/**
 * Event listener for password input.
 */
passwordInput.addEventListener('input', function() {
    const password = passwordInput.value;

    let score = calculatePasswordStrength(password);

    updatePasswordStrength(score);
});

/**
 * Calculates the strength of a password.
 * 
 * - Uses a scoring system to determine the strength of a password (0 - 5)
 * - Checks if password is at least 8 characters long, contains at least one 
 *   uppercase letter, one lowercase letter, one number, and one special character.
 * 
 * @param {string} password - The password to calculate the strength of.
 * @returns {number} - The strength of the password.
 */
function calculatePasswordStrength(password) {
    let score = 0;

    // Checks if password is at least 1 character long
    if (password.length > 0) {
        score++;
    }

    // Checks if password is at least 8 characters long
    if (password.length >= 8) {
        score++;
    }

    // Checks if password contains at least one lowercase letter and one uppercase letter
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) {
        score++;
    }

    // Checks if password contains at least one number
    if (/[0-9]/.test(password)) {
        score++;
    }

    // Checks if password contains at least one special character
    if (/[!@#$%^&*]/.test(password)) {
        score++;
    }

    return score;
}

/**
 * Updates the password strength bar and text.
 * 
 * @param {number} score - The strength of the password.
 */
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
