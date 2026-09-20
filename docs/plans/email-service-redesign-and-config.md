# 📧 Kế Hoạch Thiết Kế Lại & Cấu Hình Email Service Cá Nhân

- **Mã kế hoạch:** `PLAN-EMAIL`
- **Tập tin liên quan:**
  - `src/main/resources/application.properties`
  - `src/main/resources/application-dev.properties` (Mới)
  - `.gitignore`
  - `src/main/java/com/medical/schoolMedical/service/EmailService.java`
  - `src/main/resources/templates/common/otp-email.html`
- **Trạng thái:** ⏳ Đang chờ người dùng phê duyệt (Pending Review)

---

## 1. Thực Trạng & Mục Tiêu (Context & Objectives)

### Thực trạng hiện tại:
- Hệ thống đang dùng trực tiếp tài khoản Gmail và Mật khẩu ứng dụng của người khác trong file `application.properties`:
  ```properties
  spring.mail.username=kiritohackiem05@gmail.com
  spring.mail.password=rvidiyvfaqgzcuqb
  ```
- **Rủi ro:**
  1. Nếu chủ tài khoản này đổi mật khẩu hoặc tắt App Password, toàn bộ tính năng quên mật khẩu / gửi OTP của dự án sẽ bị lỗi (crash).
  2. Lộ thông tin tài khoản cá nhân trên repository công khai.
  3. Khi không có kết nối internet hoặc Gmail lỗi, lập trình viên không thể test được tính năng vì không nhận được OTP.

### Mục tiêu thiết kế lại:
1. **Chuyển quyền sở hữu Email:** Giúp bạn cấu hình Gmail cá nhân của bạn một cách an toàn 100%.
2. **Cơ chế Dev Mode / Fallback:** Khi đang phát triển ở môi trường local, nếu chưa cấu hình email hoặc gửi mail thất bại, hệ thống sẽ **tự động in mã OTP ra Terminal/Console log**, giúp bạn luôn test được tính năng mà không bị phụ thuộc vào mạng.
3. **Mẫu Email OTP chuyên nghiệp:** Nâng cấp template HTML `otp-email.html` với giao diện hiện đại, responsive, có thương hiệu nhà trường.
4. **Bảo mật thông tin:** Đảm bảo mật khẩu email cá nhân của bạn **không bao giờ bị push lên GitHub**.

---

## 2. Hướng Dẫn Bạn Tạo Mật Khẩu Ứng Dụng Gmail (App Password)

Để Spring Boot có thể gửi mail thông qua tài khoản Gmail của bạn, Google không cho phép dùng mật khẩu thông thường mà bắt buộc dùng **Mật khẩu ứng dụng (App Password - 16 ký tự)**:

### 🔹 Bước 1: Bật Xác minh 2 bước (2-Step Verification)
1. Truy cập vào tài khoản Google của bạn: [myaccount.google.com/security](https://myaccount.google.com/security).
2. Tìm đến mục **"Xác minh 2 bước" (2-Step Verification)** và bật nó lên (nếu bạn chưa bật).

### 🔹 Bước 2: Tạo Mật khẩu ứng dụng (App Password)
1. Sau khi đã bật xác minh 2 bước, truy cập trực tiếp đường link: [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords).
2. Nhập tên ứng dụng, ví dụ: `School Medical App`.
3. Nhấn **Tạo (Create)**.
4. Google sẽ hiển thị một mật khẩu gồm **16 ký tự chữ cái ngẫu nhiên** (dạng: `xxxx xxxx xxxx xxxx`).
5. **LƯU Ý:** Copy và lưu lại 16 ký tự này (bỏ qua khoảng trắng khi điền vào cấu hình).

---

## 3. Kiến Trúc Cấu Hình Bảo Mật Cho Dự Án

### 🔹 Cách 1: Sử dụng file `application-dev.properties` (Khuyên dùng khi dev local)
1. Thêm `src/main/resources/application-dev.properties` vào file `.gitignore` để Git không bao giờ theo dõi file này.
2. Tạo file `src/main/resources/application-dev.properties`:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-16-char-app-password
   ```
3. Trong `application.properties` chính, cấu hình:
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=${SPRING_MAIL_USERNAME:your-email@gmail.com}
   spring.mail.password=${SPRING_MAIL_PASSWORD:your-app-password}
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

### 🔹 Cách 2: Sử dụng Biến môi trường hệ điều hành (Environment Variables)
- Bạn có thể đặt trực tiếp trên máy Windows:
  ```powershell
  [System.Environment]::SetEnvironmentVariable('SPRING_MAIL_USERNAME', 'email_cua_ban@gmail.com', 'User')
  [System.Environment]::SetEnvironmentVariable('SPRING_MAIL_PASSWORD', '16_ky_tu_app_password', 'User')
  ```

---

## 4. Nâng Cấp Mã Nguồn `EmailService.java`

Hiện tại `EmailService.java` đang viết rất đơn giản và dễ văng lỗi nếu gửi mail thất bại:

```java
// Đề xuất nâng cấp trong EmailService.java:
@Slf4j
@Service
public class EmailService {
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    @Async
    public CompletableFuture<Boolean> sendOtpEmail(String to, String otp) {
        log.info("========== [OTP DEV MODE] ==========");
        log.info("Gửi OTP đến: {} | Mã OTP: {}", to, otp);
        log.info("=====================================");

        // Nếu chưa cấu hình email hoặc email rỗng -> Chỉ in ra log để dev không bị nghẽn
        if (senderEmail == null || senderEmail.isBlank() || mailSender == null) {
            log.warn("Chưa cấu hình Gmail! Mã OTP đã được in ra console để test.");
            return CompletableFuture.completedFuture(true);
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(senderEmail, "Hệ Thống Y Tế Học Đường");
            helper.setTo(to);
            helper.setSubject("Mã xác thực OTP - Quên mật khẩu");

            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("appName", "School Medical Management System");

            String htmlContent = templateEngine.process("common/otp-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Đã gửi email OTP thành công tới: {}", to);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email OTP tới {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }
}
```

---

## 5. Nâng Cấp Template `otp-email.html`

- Thiết kế lại giao diện email theo phong cách hiện đại:
  - Header có logo & tên trường học.
  - Khối mã OTP to, nổi bật, dễ copy.
  - Cảnh báo bảo mật: *"Không chia sẻ mã này với bất kỳ ai. Mã có hiệu lực trong vòng 5 phút."*
  - Footer thông tin hỗ trợ kỹ thuật.

---

## 6. Checklist Triển Khai (Action Items)

- [ ] **Bước 1:** Bạn tạo Mật khẩu ứng dụng Gmail (App Password) theo mục 2.
- [ ] **Bước 2:** Cập nhật `.gitignore` để bảo vệ file cấu hình chứa mật khẩu.
- [ ] **Bước 3:** Cập nhật `application.properties` sử dụng biến môi trường/fallback.
- [ ] **Bước 4:** Nâng cấp `EmailService.java` hỗ trợ in OTP ra log khi dev (Dev Mode) và bắt lỗi bất đồng bộ.
- [ ] **Bước 5:** Thiết kế lại giao diện HTML của email `otp-email.html`.
