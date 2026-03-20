(function() {
    const savedTheme = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
})();

document.addEventListener('DOMContentLoaded', () => {
    const themeToggle = document.getElementById('theme-toggle');
    const themeIcon = document.getElementById('theme-icon');
    
    const currentTheme = document.documentElement.getAttribute('data-theme');
    if (currentTheme === 'light' && themeIcon) {
        themeIcon.classList.replace('fa-moon', 'fa-sun');
    }

    if (themeToggle) {
        themeToggle.addEventListener('click', (e) => {
            e.preventDefault();
            
            const isLight = document.documentElement.getAttribute('data-theme') === 'light';
            const newTheme = isLight ? 'dark' : 'light';
            
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            
            if (themeIcon) {
                if (newTheme === 'light') {
                    themeIcon.classList.replace('fa-moon', 'fa-sun');
                } else {
                    themeIcon.classList.replace('fa-sun', 'fa-moon');
                }
            }
        });
    }
});