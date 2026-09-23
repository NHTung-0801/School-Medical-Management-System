// Back to Top functionality
const backToTop = document.getElementById('backToTop');

if (backToTop) {
    // Show/hide button on scroll
    window.addEventListener('scroll', () => {
        if (window.scrollY > 200) { // Show after scrolling 200px
            backToTop.style.display = 'block';
        } else {
            backToTop.style.display = 'none';
        }
    });

    // Scroll to top on click
    backToTop.addEventListener('click', () => {
        window.scrollTo({
            top: 0,
            behavior: 'smooth' // Smooth scrolling
        });
    });
}

// User & Notification Dropdown functionality
document.addEventListener('DOMContentLoaded', () => {
    // User profile dropdown toggle
    const userTrigger = document.querySelector('.user-authenticated');
    const userDropdown = document.getElementById('dropdownUser');

    if (userTrigger && userDropdown) {
        userTrigger.addEventListener('click', (e) => {
            // If clicking on an action inside the dropdown, don't toggle
            if (e.target.closest('.dropdown-item') || e.target.closest('.dropdown-logout-btn')) {
                return;
            }
            e.stopPropagation();
            userDropdown.classList.toggle('show');
            userTrigger.classList.toggle('active');

            // Close notification dropdown if open
            const notiDropdown = document.getElementById('notificationDropdown');
            if (notiDropdown) notiDropdown.classList.remove('show');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', (e) => {
            if (!userTrigger.contains(e.target)) {
                userDropdown.classList.remove('show');
                userTrigger.classList.remove('active');
            }
        });
    }

    // Notification dropdown toggle
    const notiBtn = document.getElementById('notificationBellBtn');
    const notiDropdown = document.getElementById('notificationDropdown');
    const notiContainer = document.getElementById('notificationBellContainer');

    if (notiBtn && notiDropdown) {
        notiBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            notiDropdown.classList.toggle('show');

            // Close user dropdown if open
            if (userDropdown) {
                userDropdown.classList.remove('show');
                if (userTrigger) userTrigger.classList.remove('active');
            }
        });

        document.addEventListener('click', (e) => {
            if (notiContainer && !notiContainer.contains(e.target)) {
                notiDropdown.classList.remove('show');
            }
        });
    }

    // Print functionality
    const printButton = document.querySelector('.print-button');
    if (printButton) {
        printButton.addEventListener('click', () => {
            const style = document.createElement('style');
            style.id = 'print-style';
            style.innerHTML = `
                @media print {
                    body * {
                        visibility: hidden;
                    }
                    .container, .container * {
                        visibility: visible;
                    }
                    .container {
                        position: absolute;
                        left: 0;
                        top: 0;
                        width: 100%;
                    }
                }
            `;
            document.head.appendChild(style);
            window.print();
            style.remove();
        });
    }
});