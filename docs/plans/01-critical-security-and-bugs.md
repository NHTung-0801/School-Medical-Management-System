# 🛡️ Kế Hoạch Chi Tiết Giai Đoạn 1: Vá Lỗ Hổng Bảo Mật & Sửa Lỗi Runtime

- **Mã kế hoạch:** `PLAN-01`
- **Tập tin liên quan:**
  - `src/main/java/com/medical/schoolMedical/controller/auth/OtpController.java`
  - `src/main/java/com/medical/schoolMedical/controller/user/PasswordController.java`
  - `src/main/java/com/medical/schoolMedical/service/OtpService.java`
  - `src/main/java/com/medical/schoolMedical/service/UserService.java`
  - `src/main/java/com/medical/schoolMedical/security/SecurityConfig.java`
  - `src/main/java/com/medical/schoolMedical/controller/admin/AdminController.java`
  - `src/main/java/com/medical/schoolMedical/controller/user/HealthRecordController.java`
  - `src/main/java/com/medical/schoolMedical/controller/parent/SentMedicineController.java`
  - `src/main/java/com/medical/schoolMedical/repositories/MedicalEventRepository.java`
  - `src/main/java/com/medical/schoolMedical/entities/MedicalEvent.java`
  - `src/main/resources/application.properties`
- **Trạng thái:** ⏳ Đang chờ người dùng phê duyệt (Pending Review)

---

## 1. Mục Tiêu (Objectives)

1. **Xóa bỏ hoàn toàn nguy cơ Chiếm quyền tài khoản (Account Takeover):** Thiết lập cơ chế Token đặt lại mật khẩu an toàn (`PasswordResetToken`), có hiệu lực ngắn (5 phút) và bắt buộc phải xác thực OTP thành công.
2. **Khắc phục lỗi IDOR (Insecure Direct Object Reference):** Đảm bảo phụ huynh chỉ có thể thao tác với hồ sơ và gửi thuốc cho con của chính mình.
3. **Kích hoạt lại CSRF & Chuẩn hóa HTTP Methods:** Chuyển toàn bộ các endpoint xóa dữ liệu từ `GET` sang `POST`/`DELETE` và bảo vệ bằng CSRF Token.
4. **Nâng cấp độ an toàn của OTP:** Chuyển sang `SecureRandom`, tăng độ dài lên 6 số, tự hủy ngay sau khi sử dụng để chống replay attack.
5. **Khắc phục lỗi Runtime Hibernate:** Sửa lỗi `MultipleBagFetchException` trong truy vấn sự kiện y tế.
6. **Bảo vệ thông tin nhạy cảm:** Tách mật khẩu email và CSDL thành biến môi trường.

---

## 2. Các Bước Thực Hiện Chi Tiết (Step-by-Step Implementation)

### 🔹 Bước 1.1: Vá luồng Quên mật khẩu & Reset Password (Account Takeover)
- **Vấn đề:** Hiện tại `OtpController:62` redirect sang `/reset-password?userID=1`. Sau đó `PasswordController:84` nhận `userID` từ request và đổi mật khẩu trực tiếp mà không cần bất kỳ bằng chứng xác thực nào.
- **Giải pháp:**
  1. Tạo class `PasswordResetToken` (hoặc quản lý bằng `ConcurrentHashMap<String, ResetTokenData>` trong `OtpService`):
     - Chứa: `token` (UUID ngẫu nhiên), `email` / `userId`, `expiryTime` (5 phút).
  2. Tại `OtpController`: Khi `otpService.validateOtp(email, otp)` thành công:
     - Sinh `resetToken = UUID.randomUUID().toString()`.
     - Lưu `resetToken` kèm `userId` và thời hạn 5 phút.
     - Hủy mã OTP vừa dùng (tránh dùng lại).
     - Redirect: `/reset-password?token=` + `resetToken`.
  3. Tại `PasswordController`:
     - `@GetMapping("/reset-password")`: Nhận `@RequestParam("token") String token`. Kiểm tra token hợp lệ/chưa hết hạn -> Render trang `user/newpass.html` kèm trường ẩn `token`. Nếu token không hợp lệ -> báo lỗi và quay về `/forgot-password`.
     - `@PostMapping("/reset-password")`: Nhận `@RequestParam("token") String token`, `@RequestParam("password") String newPassword`, `@RequestParam("confirm-password") String confirmPassword`.
     - Xác thực token: Lấy `userId` từ token. Nếu token hợp lệ -> đổi mật khẩu cho `userId` đó -> **Hủy ngay token** -> Chuyển hướng sang `/login` với thông báo thành công.

---

### 🔹 Bước 1.2: Nâng cấp `OtpService` chuẩn bảo mật
- **Vấn đề:** Dùng `java.util.Random`, OTP 4 ký tự, hủy OTP bằng timer cố định gây race condition nếu người dùng bấm "Gửi lại OTP".
- **Giải pháp:**
  1. Thay `Random` bằng `java.security.SecureRandom`.
  2. Đổi `OTP_LENGTH = 6`.
  3. Tăng thời gian hiệu lực `OTP_VALIDITY_MINUTES = 3` (hoặc 5 phút) để phù hợp độ trễ gửi email.
  4. Trong `validateOtp(email, otp)`: Nếu đúng, gọi `otpStorage.remove(email)` để không thể dùng lại mã OTP này.
  5. Khi người dùng bấm gửi lại (resend): Ghi đè OTP mới kèm timestamp mới, kiểm tra hết hạn bằng `System.currentTimeMillis() > expiryTime` thay vì phụ thuộc duy nhất vào `scheduler`.

---

### 🔹 Bước 1.3: Sửa lỗi Hibernate `MultipleBagFetchException`
- **Vấn đề:** [MedicalEventRepository.java:15-19](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/repositories/MedicalEventRepository.java#L15-L19) thực hiện `JOIN FETCH` đồng thời hai collection kiểu `List`: `medicineUsed` và `supplyUsed`. Hibernate sẽ ném ngoại lệ `MultipleBagFetchException` khi chạy.
- **Giải pháp:**
  - Trong [MedicalEvent.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/entities/MedicalEvent.java):
    - Đổi kiểu dữ liệu từ `List<MedicineUsed>` sang `Set<MedicineUsed>`.
    - Đổi kiểu dữ liệu từ `List<SupplyUsed>` sang `Set<SupplyUsed>`.
  - Đây là giải pháp chuẩn trong JPA/Hibernate để cho phép nạp đồng thời nhiều quan hệ One-to-Many trong cùng một câu lệnh truy vấn mà không bị lỗi.

---

### 🔹 Bước 1.4: Vá lỗi IDOR & Kiểm tra quyền sở hữu
- **Vấn đề:** Phụ huynh A có thể xóa hồ sơ học sinh của phụ huynh B, hoặc gửi thuốc cho học sinh bất kỳ.
- **Giải pháp:**
  1. Trong [HealthRecordController.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/controller/user/HealthRecordController.java):
     - Tại hàm xóa: Tìm bản ghi `HealthRecord`, kiểm tra `record.getParent().getId().equals(currentParent.getId())`. Nếu không khớp -> ném lỗi hoặc từ chối truy cập.
     - Chuyển `@GetMapping("/delete/{id}")` thành `@PostMapping("/delete/{id}")`.
  2. Trong [SentMedicineController.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/controller/parent/SentMedicineController.java):
     - Tại hàm `submitSentMedicine`: Kiểm tra xem `student.getParent().getId().equals(parent.getId())`. Nếu không đúng -> ném ngoại lệ `BusinessException(ErrorCode.UNAUTHORIZED)`.

---

### 🔹 Bước 1.5: Bật lại CSRF & Chuẩn hóa các Endpoint Xóa
- **Vấn đề:** `csrf().disable()` bị tắt trong [SecurityConfig.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/security/SecurityConfig.java), các nút xóa dùng link thẻ `<a>` (GET).
- **Giải pháp:**
  1. Trong [AdminController.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/controller/admin/AdminController.java): Đổi `@GetMapping("/delete-user/{id}")` thành `@PostMapping("/delete-user/{id}")`.
  2. Bật lại CSRF trong [SecurityConfig.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/security/SecurityConfig.java) và đảm bảo các form Thymeleaf tự động chèn CSRF token.

---

### 🔹 Bước 1.6: Tách cấu hình nhạy cảm sang Biến môi trường
- Trong [application.properties](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/resources/application.properties):
  - `spring.mail.username=${SPRING_MAIL_USERNAME:}`
  - `spring.mail.password=${SPRING_MAIL_PASSWORD:}`
  - `spring.datasource.password=${DB_PASSWORD:root}`

---

## 3. Kế Hoạch Kiểm Tra & Xác Minh (Verification Plan)

1. **Kiểm tra luồng Reset Password:**
   - Thử gửi request `POST /reset-password?userID=1` mà không có token -> Bắt buộc bị từ chối.
   - Thử luồng quên mật khẩu bình thường: Nhập email -> Nhận OTP -> Nhập đúng OTP -> Nhận Token -> Đổi mật khẩu thành công.
   - Thử dùng lại Token hoặc OTP cũ -> Bắt buộc báo hết hạn/không hợp lệ.
2. **Kiểm tra Hibernate `MultipleBagFetchException`:**
   - Chạy hàm `medicalEventRepository.findByIdWithDetails(id)` -> Đảm bảo trả về đúng dữ liệu mà không văng ngoại lệ.
3. **Kiểm tra IDOR:**
   - Đăng nhập tài khoản phụ huynh A, cố tình gửi thuốc hoặc xóa hồ sơ của con phụ huynh B -> Đảm bảo bị chặn.
4. **Kiểm tra CSRF:**
   - Thử gửi POST request không có CSRF token -> Bị từ chối (403 Forbidden).
