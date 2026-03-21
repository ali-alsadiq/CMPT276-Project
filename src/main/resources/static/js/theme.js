(function() {
    const savedTheme = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
})();

document.addEventListener('DOMContentLoaded', () => {
    const themeToggle = document.getElementById('theme-toggle');
    const themeIcon = document.getElementById('theme-icon');

    // Exit if page has no theme toggle button or icon
    if (!themeToggle || !themeIcon) return;

    // Update icon based on theme
    const updateIcon = (theme) => {
        themeIcon.classList.toggle('fa-sun', theme == 'light');
        themeIcon.classList.toggle('fa-moon', theme == 'dark');
    };

    updateIcon(document.documentElement.getAttribute('data-theme'));

    // Toggle theme when button is clicked
    themeToggle.addEventListener('click', (e) => {
        e.preventDefault();

        const newTheme = document.documentElement.getAttribute('data-theme') == 'light' ? 'dark' : 'light';

        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateIcon(newTheme);
    });
}); 