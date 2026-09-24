# Kế Hoạch Hoàn Thiện Email Service & Cấu Hình Email Cá Nhân Của Người Dùng

## 1. Bối Cảnh & Rà Soát Hiện Trạng Hệ Thống (Code Review)

### A. Rà soát kiến trúc Email hiện tại:
- **Thư viện:** Dự án đã có sẵn `spring-boot-starter-mail` trong [pom.xml](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/pom.xml).
- **Service gửi email:** [EmailService.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/modules/auth/services/EmailService.java):
  - Phương thức `@Async public void sendOtpEmail(String to, String otp)` xử lý gửi bất đồng bộ để không làm treo giao diện người dùng.
  - Sử dụng template Thymeleaf chuyên nghiệp [otp-email.html](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/resources/templates/common/otp-email.html).
  - Có cơ chế **Dev Mode Fallback**: Khi chưa cấu hình email hoặc gửi mail thất bại, hệ thống tự động in mã OTP ra console log (`📧 [DEV MODE OTP] Gửi tới: ... | MÃ OTP: ...`) giúp lập trình viên vẫn tiếp tục test được các tính năng.
- **Nghiệp vụ sử dụng:**
  - [OtpController.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/modules/auth/controllers/OtpController.java): Gọi `emailService.sendOtpEmail(email, otp)` trong luồng Quên mật khẩu (`/yte/forgot-password` $\rightarrow$ `/yte/new-password`).
  - [OtpService.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/modules/auth/services/OtpService.java): Quản lý OTP 6 số ngẫu nhiên, tự hủy sau 3 phút, chống Replay Attack và sinh Reset Token 5 phút.

### B. Điểm cần tối ưu hóa:
1. Trong [application.properties](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/resources/application.properties), cấu hình email đang đọc từ biến môi trường trống:
   ```properties
   spring.mail.username=${SPRING_MAIL_USERNAME:}
   spring.mail.password=${SPRING_MAIL_PASSWORD:}
   ```
2. Chưa cấu hình timeout kết nối SMTP (nếu mạng yếu hoặc proxy chặn port 587 có thể gây treo thread gửi mail nếu không có timeout).
3. Cần cơ chế nạp cấu hình email cá nhân của người dùng tiện lợi nhất khi chạy local mà **không bao giờ bị lộ mật khẩu lên GitHub** (Tuân thủ nghiêm ngặt **Quy tắc 7 trong AGENTS.md**).

---

## 2. Đề Xuất Các Phương Án Cấu Hình Email Cá Nhân (Multi-Option Solutions)

Để đưa email cá nhân của bạn vào hoạt động mà vẫn đảm bảo an toàn bí mật tuyệt đối:

### 🌟 Phương Án A (Khuyến nghị - Recommended): Cấu hình qua file `application-dev.properties`
- **Cách thực hiện:**
  - Thêm cấu hình nạp file tùy chọn trong `application.properties`:
    ```properties
    spring.config.import=optional:classpath:application-dev.properties
    ```
  - Tạo file mẫu `src/main/resources/application-dev.properties.example`.
  - Người dùng tạo file local `src/main/resources/application-dev.properties` chứa:
    ```properties
    spring.mail.username=email_cua_ban@gmail.com
    spring.mail.password=16_ky_tu_app_password
    ```
- **Ưu điểm:**
  - Cực kỳ tiện lợi, chỉ cần tạo file 1 lần là Spring Boot tự động nhận diện khi chạy local.
  - File `application-dev.properties` **đã nằm trong `.gitignore`**, đảm bảo 100% không bao giờ bị push lên GitHub.
  - Từ khóa `optional:` đảm bảo môi trường CI/CD (GitHub Actions) không bị lỗi dù file này không tồn tại trên remote.
- **Rủi ro:** Không có.

### 🔹 Phương Án B: Cấu hình qua Biến môi trường hệ thống Windows (Environment Variables)
- **Cách thực hiện:**
  - Người dùng mở PowerShell và chạy:
    ```powershell
    [System.Environment]::SetEnvironmentVariable('SPRING_MAIL_USERNAME', 'email_cua_ban@gmail.com', 'User')
    [System.Environment]::SetEnvironmentVariable('SPRING_MAIL_PASSWORD', '16_ky_tu_app_password', 'User')
    ```
- **Ưu điểm:** Không cần tạo thêm file nào trong thư mục dự án.
- **Nhược điểm:** Người dùng phải khởi động lại toàn bộ IDE/Terminal thì hệ thống mới nhận biến môi trường mới.

---

## 3. Hướng Dẫn Bạn Lấy Mật Khẩu Ứng Dụng Gmail (App Password 16 Ký Tự)

Để gửi email tự động qua Gmail, Google bắt buộc sử dụng **Mật khẩu ứng dụng (App Password)** gồm 16 chữ cái, thay vì mật khẩu đăng nhập thông thường:

1. **Bật xác minh 2 bước (2-Step Verification):**
   - Vào [myaccount.google.com/security](https://myaccount.google.com/security) $\rightarrow$ Tìm mục **Xác minh 2 bước** và bật lên (nếu chưa bật).
2. **Tạo mật khẩu ứng dụng:**
   - Vào trực tiếp đường dẫn: [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords).
   - Đặt tên ứng dụng (ví dụ: `School Medical`).
   - Nhấn **Tạo (Create)** $\rightarrow$ Google sẽ cấp một chuỗi 16 chữ cái (dạng `abcd efgh ijkl mnop`).
   - Copy lại 16 chữ cái này (bỏ dấu cách).

---

## 4. Kế Hoạch Triển Khai Chi Tiết (Implementation Steps)

1. **Bước 1: Tối ưu cấu hình SMTP trong `application.properties`:**
   - Thêm `spring.config.import=optional:classpath:application-dev.properties`.
   - Bổ sung timeout kết nối SMTP.
2. **Bước 2: Tạo file mẫu `application-dev.properties.example`:**
   - Chứa hướng dẫn mẫu để bạn chỉ việc copy và điền email cá nhân.
3. **Bước 3: Tối ưu `EmailService.java`:**
   - Bổ sung log trạng thái rõ ràng, phân biệt giữa chế độ gửi email thật (Production/Personal) và chế độ giả lập (Dev Console Fallback).
   - Đảm bảo hiển thị tên người gửi trang trọng: `"Hệ Thống Y Tế Học Đường <email_cua_ban@gmail.com>"`.
4. **Bước 4: Bổ sung Unit Test cho `EmailServiceTest`:**
   - Kiểm thử gửi email thành công với `JavaMailSender` mock.
   - Kiểm thử cơ chế fallback khi `spring.mail.username` rỗng.
5. **Bước 5: Hướng dẫn người dùng cấu hình và chạy thử:**
   - Cung cấp hướng dẫn ngắn gọn để bạn tạo file `application-dev.properties` với email và app password của bạn.
   - Chạy lệnh test thực tế bằng cách thao tác trên giao diện Quên mật khẩu.

---

## 5. Tiêu Chí Nghiệm Thu (Verification Gate)
- `.\mvnw.cmd test` biên dịch thành công, toàn bộ test suite (bao gồm `EmailServiceTest`) đạt `BUILD SUCCESS`.
- Khi bạn cấu hình email cá nhân: Email OTP gửi thành công vào đúng hòm thư Gmail của bạn trong vòng vài giây.
