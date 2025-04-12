/**
 * 
 */
// Password toggle functionality
document.querySelectorAll('.toggle-password').forEach(button => {
    button.addEventListener('click', function() {
        const input = this.parentElement.querySelector('input');
        const icon = this.querySelector('i');
        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.toggle("fa-eye");
icon.classList.toggle("fa-eye-slash");

        } else {
            input.type = 'password';
            icon.classList.toggle("fa-eye");
icon.classList.toggle("fa-eye-slash");

        }
    });
});

// Password strength meter
const passwordInput = document.getElementById('newPassword');
const strengthBar = document.querySelector('.password-strength .progress-bar');
const strengthText = document.querySelector('.password-strength-text');

passwordInput.addEventListener('input', function() {
    const strength = calculatePasswordStrength(this.value);
    strengthBar.style.width = strength.percentage + '%';
    strengthBar.className = 'progress-bar ' + strength.class;
    strengthText.textContent = strength.text;
    strengthText.className = 'text-muted password-strength-text ' + strength.class;
});

function calculatePasswordStrength(password) {
    // Implement your password strength logic here
    let score = 0;
    if (password.length >= 8) score += 25;
    if (/[A-Z]/.test(password)) score += 25;
    if (/\d/.test(password)) score += 25;
    if (/[^A-Za-z0-9]/.test(password)) score += 25;
    
    let text, cls;
    if (score >= 75) {
        text = "Strong password";
        cls = "text-success";
    } else if (score >= 50) {
        text = "Good password";
        cls = "text-info";
    } else if (score >= 25) {
        text = "Weak password";
        cls = "text-warning";
    } else {
        text = "Very weak password";
        cls = "text-danger";
    }
    
    return {
        percentage: score,
        class: cls,
        text: text
    };
}

// Password matching validation
const newPassword = document.getElementById('newPassword');
const confirmPassword = document.getElementById('confirmPassword');

function validatePasswordMatch() {
    if (newPassword.value !== confirmPassword.value) {
        confirmPassword.classList.add('is-invalid');
    } else {
        confirmPassword.classList.remove('is-invalid');
    }
}

newPassword.addEventListener('input', validatePasswordMatch);
confirmPassword.addEventListener('input', validatePasswordMatch);

// Delete account confirmation
const finalConfirmation = document.getElementById('finalConfirmation');
const finalDeleteBtn = document.getElementById('finalDeleteBtn');

finalConfirmation.addEventListener('input', function() {
    finalDeleteBtn.disabled = this.value !== "DELETE MY ACCOUNT";
});

// Tab switching helper
function switchTab(tabId) {
    const tab = document.getElementById(tabId);
    const tabInstance = new bootstrap.Tab(tab);
    tabInstance.show();
}