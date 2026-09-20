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

//print
document.addEventListener('DOMContentLoaded', () => {
    const printButton = document.querySelector('.print-button');
    if (printButton) {
        printButton.addEventListener('click', () => {
        // Add a temporary style to hide everything except .container
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

        // Trigger the print dialog
        window.print();

        // Remove the temporary style after printing
        style.remove();
    });
});