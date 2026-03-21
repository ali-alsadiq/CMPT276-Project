document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.getElementById('dynamic-sidebar');
    const delayInMilliseconds = 500; 
    let expandTimer; 

    if (!sidebar) return;

    // Check if the navbar was expanded before changing tabs
    if (sessionStorage.getItem('sidebarWasOpen') === 'true') {
        // Force it open instantly without waiting for the hover timer
        sidebar.classList.add('is-expanded');
        
        sessionStorage.removeItem('sidebarWasOpen'); // Clear the temp memory
    }

    // Saving the state on click
    const navLinks = sidebar.querySelectorAll('a');
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (sidebar.classList.contains('is-expanded')) {
                // Save state temporarily in memory
                sessionStorage.setItem('sidebarWasOpen', 'true');
            }
        });
    });

    // Mouse hover to expand
    sidebar.addEventListener('mouseenter', () => {
        expandTimer = setTimeout(() => {
            sidebar.classList.add('is-expanded');
        }, delayInMilliseconds); 
    });

    sidebar.addEventListener('mouseleave', () => {
        clearTimeout(expandTimer);
        sidebar.classList.remove('is-expanded');
    });
});