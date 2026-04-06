// script to filter leaderboard by search bar

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('friendSearch');
    const friendsGrid = document.getElementById('friendsGrid');
    
    if (!searchInput || !friendsGrid) return;
    
    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase().trim();
        const friendCards = friendsGrid.querySelectorAll('.friend-card');
        
        let visibleCount = 0;
        
        friendCards.forEach(card => {
            // Get friend info
            const username = card.getAttribute('data-username') || '';
            const firstname = card.getAttribute('data-firstname') || '';
            const lastname = card.getAttribute('data-lastname') || '';
            const rr = card.getAttribute('data-rr') || '';
            
            // Check against all to improve search
            const matchesSearch = searchTerm === '' || 
                username.toLowerCase().includes(searchTerm) ||
                firstname.toLowerCase().includes(searchTerm) ||
                lastname.toLowerCase().includes(searchTerm) ||
                rr.includes(searchTerm);
            
            if (matchesSearch) {
                card.style.display = '';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        });
        
        // no results msg
        let noResultsMsg = friendsGrid.querySelector('.no-results-message');
        if (visibleCount === 0 && searchTerm !== '') {
            if (!noResultsMsg) {
                noResultsMsg = document.createElement('div');
                noResultsMsg.className = 'text-center py-5 w-100 no-results-message';
                noResultsMsg.innerHTML = `
                    <i class="fa-solid fa-user-slash fs-1 text-white-50 mb-3"></i>
                    <p class="text-white-50 mb-0">No friends found matching "${searchTerm}"</p>
                `;
                friendsGrid.appendChild(noResultsMsg);
            }
        } else {
            if (noResultsMsg) {
                noResultsMsg.remove();
            }
        }
    });
});