document.addEventListener('DOMContentLoaded', () => {
    // Accordion (Tài liệu)
    const accordionTitles = document.querySelectorAll('.accordion-title');
    accordionTitles.forEach(title => {
        title.addEventListener('click', () => {
            const item = title.parentElement;
            const content = item.querySelector('.accordion-content');
            const icon = title.querySelector('i');

            item.classList.toggle('active');
            if (item.classList.contains('active')) {
                content.style.display = 'block';
                content.style.opacity = '0';
                setTimeout(() => {
                    content.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
                    content.style.opacity = '1';
                    content.style.transform = 'perspective(500px) translateZ(0)';
                }, 50);
                icon.classList.remove('fa-plus');
                icon.classList.add('fa-minus');
            } else {
                content.style.opacity = '0';
                content.style.transform = 'perspective(500px) translateZ(-10px)';
                setTimeout(() => {
                    content.style.display = 'none';
                }, 600);
                icon.classList.remove('fa-minus');
                icon.classList.add('fa-plus');
            }
        });
    });

    // FAQ (giữ nguyên nếu có)
    const faqQuestions = document.querySelectorAll('.faq-question');
    faqQuestions.forEach(question => {
        question.addEventListener('click', () => {
            const item = question.parentElement;
            const answer = item.querySelector('.faq-answer');
            const icon = question.querySelector('i');

            item.classList.toggle('active');
            if (item.classList.contains('active')) {
                answer.style.display = 'block';
                answer.style.opacity = '0';
                setTimeout(() => {
                    answer.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
                    answer.style.opacity = '1';
                    answer.style.transform = 'perspective(500px) translateZ(0)';
                }, 50);
                icon.classList.remove('fa-plus');
                icon.classList.add('fa-minus');
            } else {
                answer.style.opacity = '0';
                answer.style.transform = 'perspective(500px) translateZ(-10px)';
                setTimeout(() => {
                    answer.style.display = 'none';
                }, 600);
                icon.classList.remove('fa-minus');
                icon.classList.add('fa-plus');
            }
        });
    });

    // Cập nhật thời gian thực (giữ nguyên nếu có)
    function updateTime() {
        const timeElement = document.getElementById('current-time');
        if (timeElement) {
            const now = new Date();
            timeElement.textContent = now.toLocaleString('en-US', {
                hour: '2-digit',
                minute: '2-digit',
                hour12: true,
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            }) + ' +07';
        }
    }
    updateTime();
    setInterval(updateTime, 60000);

    // =========================================================
    // 1. Scroll-Driven Reveal (Lướt đến đâu nội dung hiển thị đến đó)
    // =========================================================
    const revealElements = document.querySelectorAll('.reveal-on-scroll');
    if ('IntersectionObserver' in window) {
        const revealObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('is-visible');
                    observer.unobserve(entry.target); // Chỉ kích hoạt một lần mượt mà
                }
            });
        }, {
            threshold: 0.12,
            rootMargin: '0px 0px -40px 0px'
        });

        revealElements.forEach(el => revealObserver.observe(el));
    } else {
        // Fallback cho trình duyệt cũ
        revealElements.forEach(el => el.classList.add('is-visible'));
    }

    // =========================================================
    // 2. 3D Interactive Card Tilt (Nghiêng 3D & Phản Quang Theo Chuột)
    // =========================================================
    const isTouchDevice = 'ontouchstart' in window || navigator.maxTouchPoints > 0;
    if (!isTouchDevice) {
        const tiltCards = document.querySelectorAll('.enhanced-card');
        tiltCards.forEach(card => {
            let isHovered = false;

            card.addEventListener('mouseenter', () => {
                isHovered = true;
            });

            card.addEventListener('mousemove', (e) => {
                if (!isHovered) return;
                const rect = card.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const y = e.clientY - rect.top;

                const centerX = rect.width / 2;
                const centerY = rect.height / 2;

                // Góc nghiêng tối đa ±8 độ để vừa mắt và chuyên nghiệp
                const rotateX = ((centerY - y) / centerY) * 8.5;
                const rotateY = ((x - centerX) / centerX) * 8.5;

                card.style.transform = `perspective(1000px) rotateX(${rotateX.toFixed(2)}deg) rotateY(${rotateY.toFixed(2)}deg) translateY(-8px) translateZ(12px)`;

                // Cập nhật tâm phản chiếu ánh sáng specular glare
                const mousePercentX = ((x / rect.width) * 100).toFixed(1);
                const mousePercentY = ((y / rect.height) * 100).toFixed(1);
                card.style.setProperty('--mouse-x', `${mousePercentX}%`);
                card.style.setProperty('--mouse-y', `${mousePercentY}%`);
            });

            card.addEventListener('mouseleave', () => {
                isHovered = false;
                card.style.transform = 'perspective(1000px) rotateX(0deg) rotateY(0deg) translateY(0) translateZ(0)';
                card.style.setProperty('--mouse-x', '50%');
                card.style.setProperty('--mouse-y', '50%');
            });
        });
    }
});