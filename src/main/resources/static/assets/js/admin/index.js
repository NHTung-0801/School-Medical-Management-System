document.addEventListener('DOMContentLoaded', () => {
    // Nhãn 12 tháng tiếng Việt
    const monthLabels = ['Thg 1', 'Thg 2', 'Thg 3', 'Thg 4', 'Thg 5', 'Thg 6', 'Thg 7', 'Thg 8', 'Thg 9', 'Thg 10', 'Thg 11', 'Thg 12'];

    // Dữ liệu an toàn
    const safeHealthCounts = (typeof healthCheckCounts !== 'undefined' && Array.isArray(healthCheckCounts) && healthCheckCounts.length === 12)
        ? healthCheckCounts : [0,0,0,0,0,0,0,0,0,0,0,0];
    const safeVaccinationCounts = (typeof vaccinationCounts !== 'undefined' && Array.isArray(vaccinationCounts) && vaccinationCounts.length === 12)
        ? vaccinationCounts : [0,0,0,0,0,0,0,0,0,0,0,0];
    const safeEventCounts = (typeof medicalEventCounts !== 'undefined' && Array.isArray(medicalEventCounts) && medicalEventCounts.length === 12)
        ? medicalEventCounts : [0,0,0,0,0,0,0,0,0,0,0,0];

    // --- BIỂU ĐỒ 1: Khám sức khỏe & Tiêm chủng (Line Chart Đôi) ---
    const chartCanvas1 = document.getElementById('myChart');
    if (chartCanvas1) {
        const ctx1 = chartCanvas1.getContext('2d');

        new Chart(ctx1, {
            type: 'line',
            data: {
                labels: monthLabels,
                datasets: [
                    {
                        label: 'Khám sức khỏe',
                        data: safeHealthCounts,
                        borderColor: '#0d9488', // Medical Teal
                        backgroundColor: 'rgba(13, 148, 136, 0.08)',
                        fill: true,
                        borderWidth: 2.5,
                        tension: 0.35,
                        pointBackgroundColor: '#ffffff',
                        pointBorderColor: '#0d9488',
                        pointBorderWidth: 2,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    },
                    {
                        label: 'Tiêm chủng',
                        data: safeVaccinationCounts,
                        borderColor: '#e11d48', // Medical Rose
                        backgroundColor: 'rgba(225, 29, 72, 0.08)',
                        fill: true,
                        borderWidth: 2.5,
                        tension: 0.35,
                        pointBackgroundColor: '#ffffff',
                        pointBorderColor: '#e11d48',
                        pointBorderWidth: 2,
                        pointRadius: 4,
                        pointHoverRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    mode: 'index',
                    intersect: false,
                },
                plugins: {
                    legend: {
                        position: 'top',
                        labels: {
                            boxWidth: 12,
                            font: { family: 'Poppins', size: 12, weight: '500' },
                            usePointStyle: true,
                            pointStyle: 'circle'
                        }
                    },
                    tooltip: {
                        backgroundColor: '#0f172a',
                        titleFont: { family: 'Poppins', size: 12 },
                        bodyFont: { family: 'Poppins', size: 12 },
                        padding: 10,
                        cornerRadius: 8
                    }
                },
                scales: {
                    x: {
                        grid: { display: false }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { precision: 0 },
                        grid: { color: 'rgba(0, 0, 0, 0.04)' }
                    }
                }
            }
        });
    }

    // --- BIỂU ĐỒ 2: Ca Sự Kiện Y Tế (Bar Chart) ---
    const chartCanvas2 = document.getElementById('eventStatsChart');
    if (chartCanvas2) {
        const ctx2 = chartCanvas2.getContext('2d');

        new Chart(ctx2, {
            type: 'bar',
            data: {
                labels: monthLabels,
                datasets: [{
                    label: 'Số ca sự kiện',
                    data: safeEventCounts,
                    backgroundColor: 'rgba(139, 92, 246, 0.75)', // Soft Violet
                    hoverBackgroundColor: '#8b5cf6',
                    borderRadius: 6,
                    borderSkipped: false,
                    maxBarThickness: 28
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: '#0f172a',
                        titleFont: { family: 'Poppins', size: 12 },
                        bodyFont: { family: 'Poppins', size: 12 },
                        padding: 10,
                        cornerRadius: 8
                    }
                },
                scales: {
                    x: {
                        grid: { display: false }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { precision: 0 },
                        grid: { color: 'rgba(0, 0, 0, 0.04)' }
                    }
                }
            }
        });
    }
});

// Chuyển tab danh sách học sinh
function switchTab(tab) {
    const buttons = document.querySelectorAll('.tab-button');
    buttons.forEach(btn => btn.classList.remove('active'));

    const thisMonthTable = document.getElementById('thisMonthTable');
    const lastMonthTable = document.getElementById('lastMonthTable');

    if (tab === 'thisMonth') {
        if (thisMonthTable) thisMonthTable.style.display = '';
        if (lastMonthTable) lastMonthTable.style.display = 'none';
        if (buttons[0]) buttons[0].classList.add('active');
    } else if (tab === 'lastMonth') {
        if (thisMonthTable) thisMonthTable.style.display = 'none';
        if (lastMonthTable) lastMonthTable.style.display = '';
        if (buttons[1]) buttons[1].classList.add('active');
    }
}
