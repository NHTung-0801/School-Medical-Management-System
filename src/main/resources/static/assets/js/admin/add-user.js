document.addEventListener('DOMContentLoaded', function() {
    const togglePassword = document.getElementById('togglePassword');
    const password = document.getElementById('password');

    // Toggle hiển thị/ẩn mật khẩu
    if (togglePassword && password) {
        togglePassword.addEventListener('click', function () {
            const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
            password.setAttribute('type', type);
            this.classList.toggle('fa-eye-slash');
        });
    }

    // Xử lý định dạng và kiểm tra số điện thoại đăng nhập
    const usernameInput = document.getElementById('username');
    const roleSelect = document.getElementById('role');
    const form = usernameInput ? usernameInput.closest('form') : null;

    if (usernameInput && roleSelect) {
        function updateUsernameUI() {
            const role = roleSelect.value;
            const phoneHint = document.getElementById('phoneHint');
            if (role === 'PARENT') {
                usernameInput.setAttribute('type', 'tel');
                usernameInput.setAttribute('maxlength', '10');
                usernameInput.setAttribute('placeholder', 'Nhập số điện thoại cấp cho phụ huynh (VD: 0912345678)');
                if (phoneHint) {
                    phoneHint.innerHTML = '<i class="fas fa-info-circle"></i> Số điện thoại dùng làm tài khoản đăng nhập cho phụ huynh (10 chữ số: 03, 05, 07, 08, 09).';
                }
            } else {
                usernameInput.setAttribute('placeholder', 'Nhập tên đăng nhập hoặc số điện thoại');
                if (phoneHint) {
                    phoneHint.innerHTML = '<i class="fas fa-info-circle"></i> Tên tài khoản đăng nhập hệ thống.';
                }
            }
        }

        roleSelect.addEventListener('change', updateUsernameUI);
        updateUsernameUI();

        usernameInput.addEventListener('input', function() {
            if (roleSelect.value === 'PARENT') {
                this.value = this.value.replace(/\D/g, '').slice(0, 10);
            }
        });

        if (form) {
            form.addEventListener('submit', function(e) {
                if (roleSelect.value === 'PARENT') {
                    const phoneVal = usernameInput.value.trim();
                    const phoneRegex = /^0[35789]\d{8}$/;
                    if (!phoneRegex.test(phoneVal)) {
                        e.preventDefault();
                        alert('Số điện thoại đăng nhập cho phụ huynh phải hợp lệ gồm 10 chữ số (bắt đầu bằng 03, 05, 07, 08, 09)!');
                        usernameInput.focus();
                    }
                }
            });
        }
    }
});