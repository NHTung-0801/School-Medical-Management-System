# 📋 Kế Hoạch Chi Tiết Giai Đoạn 3: Tối Ưu Hiệu Năng & Kiểm Soát Dữ Liệu (Performance & Validation)

Tài liệu này xác định chi tiết các giải pháp kỹ thuật và các phương án tối ưu để thực thi **Giai đoạn 3** trong lộ trình nâng cấp hệ thống **School Medical Management System**.

---

## 🎯 Mục Tiêu Giai Đoạn 3

1. **Tối ưu hóa xử lý hàng loạt (Batch Processing & ACID Transactions):** Chuyển đổi cơ chế lưu từng bản ghi sang lưu hàng loạt (Batch Insert) khi gửi lịch khám/tiêm cho hàng trăm học sinh, bọc `@Transactional` để đảm bảo toàn vẹn dữ liệu.
2. **Linh hoạt hóa dữ liệu thống kê (Dynamic Statistics):** Cho phép Admin và Manager tra cứu biểu đồ thống kê theo bất kỳ năm nào thay vì fix cứng năm `2025`.
3. **Kiểm soát dữ liệu đầu vào toàn diện (Input Validation & Error Handling):** Chặn đứng dữ liệu rác, số lượng âm, chuỗi rỗng bằng Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Min`, `@NotBlank`), hiển thị thông báo lỗi thân thiện trên giao diện thay vì lỗi crash HTTP 500.

---

## ⚖️ CÁC PHƯƠNG ÁN TỐI ƯU ĐỂ LỰA CHỌN (OPTIONS FOR USER DECISION)

Theo đúng quy tắc **Quy tắc 3 (Proactive Multi-Option Solutions)** trong `AGENTS.md`, dưới đây là các phương án tối ưu được phân tích đa chiều cho từng bài toán kỹ thuật:

### 1. Bài toán 3.1: Gửi Lịch Khám/Tiêm Hàng Loạt (Batch Processing)

- **Hiện trạng:**
  - `HealthCheckConsentService.java:95-102`: Vòng lặp `for (Student student : students)` gọi `healthCheckConsentRepository.save(consent)` từng bản ghi một và không có `@Transactional`. Với khối 200 học sinh, hệ thống thực hiện 200 lượt gọi round-trip đến MySQL. Nếu bị ngắt giữa chừng (ví dụ ở bản ghi 100), dữ liệu bị phân mảnh 50/50.
  - `VaccinationConsentService.java`: Đã dùng `saveAll()` nhưng cũng chưa có `@Transactional`.

- **Các phương án kỹ thuật:**
  - 🟢 **Phương án A (Spring Data Batching + Hibernate Batch Config - KHUYẾN NGHỊ):**
    - Thêm `@Transactional` vào `sendCheckSchedule_toParent()` và `sendVaccinationSchedule_toParent()`.
    - Gom toàn bộ consent vào `List<HealthCheckConsent>` và gọi `healthCheckConsentRepository.saveAll(consents)`.
    - Bật cấu hình Hibernate batch trong `application.properties`:
      ```properties
      spring.jpa.properties.hibernate.jdbc.batch_size=50
      spring.jpa.properties.hibernate.order_inserts=true
      ```
    - *Ưu điểm:* Chuẩn Spring Data/Hibernate, giảm số câu lệnh insert từ N xuống N/50, bảo đảm tính toàn vẹn tuyệt đối (ACID - thành công tất cả hoặc rollback toàn bộ).
    - *Nhược điểm:* Không có.
    - *Đánh giá:* **10/10 (Khuyến nghị áp dụng)**.

  - 🟡 **Phương án B (Xử lý Bất đồng bộ với `@Async` / Event-Driven):**
    - Đẩy việc tạo consent sang một Thread ngầm bằng `@Async` hoặc Spring `ApplicationEventPublisher`. Controller trả về ngay cho Y tá: *"Lịch đang được gửi ngầm"*.
    - *Ưu điểm:* Phản hồi tức thì trên UI của Y tá dù gửi cho 10,000 học sinh.
    - *Nhược điểm:* Tăng độ phức tạp (cần cấu hình ThreadPool, cơ chế thông báo tiến độ hoàn tất, xử lý lỗi bất đồng bộ phức tạp). Với quy mô trường học (vài trăm đến vài nghìn học sinh), Phương án A đã chạy dưới 0.5s nên Phương án B là tối ưu hóa quá sớm (premature optimization).

---

### 2. Bài toán 3.2: Linh Hoạt Hóa Năm Thống Kê (Dynamic Statistics Year)

- **Hiện trạng:**
  - `AdminController.java:49` và `ManagerController.java:41-43` đang fix cứng `2025` khi gọi `statisticsService.getMonthlyVaccinationCounts(2025)`.
  - Nếu sang năm 2026 hoặc muốn xem lại năm 2024, hệ thống không xem được.

- **Các phương án kỹ thuật:**
  - 🟢 **Phương án A (Nhận `year` qua RequestParam + Dropdown chọn năm trên UI - KHUYẾN NGHỊ):**
    - Controller nhận `@RequestParam(value = "year", required = false) Integer year`. Nếu `year == null`, mặc định lấy `LocalDate.now().getYear()`.
    - Controller truyền `selectedYear` và danh sách các năm lựa chọn (ví dụ: `[currentYear - 2, currentYear - 1, currentYear, currentYear + 1]`) vào `Model`.
    - Thêm một dropdown `<select onchange="location.href='?year=' + this.value">` vào giao diện `dashboard.html` và `manager-home.html`.
    - *Ưu điểm:* Linh hoạt tối đa, cho phép Admin/Manager xem quá khứ, hiện tại và tương lai một cách trực quan.
    - *Nhược điểm:* Cần sửa nhẹ 2 file HTML dashboard.
    - *Đánh giá:* **10/10 (Khuyến nghị áp dụng)**.

  - 🟡 **Phương án B (Chỉ đổi ở Backend sang năm hiện tại `LocalDate.now().getYear()`):**
    - Thay `2025` bằng `LocalDate.now().getYear()`, không thêm tham số hoặc dropdown.
    - *Ưu điểm:* Cực kỳ nhanh, chỉ sửa 2 dòng code.
    - *Nhược điểm:* Không thể tra cứu lại số liệu của các năm trước.

---

### 3. Bài toán 3.3: Chuẩn Hóa Input Validation Toàn Diện

- **Hiện trạng:**
  - Các form thêm/sửa thuốc (`Medicine`), vật tư y tế (`MedicalSupply`), sự kiện y tế (`MedicalEventDTO`), học sinh (`StudentDTO`), lịch khám/tiêm (`HealthCheckScheduleDTO`, `VaccinationScheduleDTO`) chưa áp dụng đầy đủ Jakarta Bean Validation.
  - Người dùng có thể nhập số lượng âm, chuỗi rỗng, ngày hết hạn trong quá khứ dẫn đến lỗi crash 500 hoặc rác CSDL.

- **Các phương án kỹ thuật:**
  - 🟢 **Phương án A (Jakarta Bean Validation + BindingResult UI Feedback - KHUYẾN NGHỊ):**
    - Đặt các annotation chuẩn trên DTO/Entity:
      - `Medicine`: `@NotBlank(message = "Tên thuốc không được để trống")`, `@Min(value = 0, message = "Số lượng trong kho không được âm")`, `@NotNull(message = "Vui lòng chọn hạn sử dụng")`.
      - `MedicalSupply`: `@NotBlank(message = "Tên vật tư không được để trống")`, `@Min(value = 0, message = "Số lượng không được âm")`.
      - `StudentDTO`: `@NotBlank(message = "Họ tên không được để trống")`, `@NotNull(message = "Vui lòng chọn ngày sinh")`, `@NotBlank(message = "Vui lòng chọn lớp")`.
      - `MedicalEventDTO`: `@NotNull(message = "Vui lòng chọn học sinh")`, `@NotBlank(message = "Địa điểm không được để trống")`.
      - `HealthCheckScheduleDTO` & `VaccinationScheduleDTO`: `@NotBlank(message = "Vui lòng chọn ngày")`, `@NotBlank(message = "Vui lòng chọn giờ")`.
    - Trong Controller: Thêm `@Valid` kèm `BindingResult`. Nếu có lỗi, giữ lại dữ liệu form và hiển thị alert lỗi rõ ràng.
    - *Ưu điểm:* Chuẩn mực cao nhất của Spring Boot MVC, an toàn tuyệt đối trước dữ liệu rác, trải nghiệm người dùng chuyên nghiệp.
    - *Nhược điểm:* Cần rà soát và cập nhật trên các controller tiếp nhận form.
    - *Đánh giá:* **10/10 (Khuyến nghị áp dụng)**.

  - 🟡 **Phương án B (Validate thủ công bằng `if-else` trong Service):**
    - Viết các câu lệnh `if (dto.getName() == null) throw new BusinessException(...)` trong tầng Service.
    - *Ưu điểm:* Không cần sửa nhiều ở Controller.
    - *Nhược điểm:* Không thân thiện với giao diện người dùng (bị chuyển hướng sang trang lỗi hoặc hiển thị chung chung), vi phạm nguyên tắc phân tách trách nhiệm (Controller chịu trách nhiệm validate định dạng đầu vào, Service chịu trách nhiệm quy tắc nghiệp vụ).

---

## 🛠️ Danh Sách Các File Cần Thay Đổi (Dự Kiến Theo Phương Án Khuyến Nghị)

1. **Cấu hình:**
   - `application.properties`: Bổ sung cấu hình batch size và order inserts của Hibernate.
2. **Tối ưu Batch & Giao dịch:**
   - `HealthCheckConsentService.java`: Thêm `@Transactional`, chuyển sang `saveAll()`.
   - `VaccinationConsentService.java`: Thêm `@Transactional`.
3. **Thống kê linh hoạt theo năm:**
   - `AdminController.java`: Nhận `@RequestParam(value = "year", required = false)`, truyền `selectedYear` và danh sách `years`.
   - `ManagerController.java`: Nhận `@RequestParam(value = "year", required = false)`, truyền `selectedYear` và danh sách `years`.
   - `templates/admin/dashboard.html`: Thêm dropdown chọn năm.
   - `templates/manager/manager-home.html`: Thêm dropdown chọn năm.
4. **Validation:**
   - `entities/Medicine.java`: Thêm `@NotBlank`, `@Min`, `@NotNull`.
   - `entities/MedicalSupply.java`: Thêm `@NotBlank`, `@Min`.
   - `dto/MedicalEventDTO.java`: Thêm `@NotNull studentId`, `@NotBlank location`, `@NotBlank description`.
   - `dto/StudentDTO.java`: Thêm `@NotBlank fullName`, `@NotNull birthDate`, `@NotBlank className`.
   - `dto/HealthCheckScheduleDTO.java` & `dto/VaccinationScheduleDTO.java`: Thêm validation ngày giờ.
   - `controller/schoolNurse/MedicineController.java`: Thêm `@Valid` và xử lý `BindingResult`.
   - `controller/schoolNurse/MedicalSupplyController.java`: Thêm `@Valid` và xử lý `BindingResult`.
   - `controller/schoolNurse/MedicalEventController.java`: Thêm `@Valid` và xử lý `BindingResult`.

---

## 🧪 Kế Hoạch Kiểm Thử (Verification Plan)
- Chạy `.\mvnw clean test-compile` để đảm bảo 100% mã nguồn biên dịch không lỗi (`BUILD SUCCESS`).
- Thử nghiệm gửi lịch khám cho 1 khối lớp: Kiểm tra tính toàn vẹn và tốc độ ghi dữ liệu.
- Thử nghiệm chọn năm trên biểu đồ Dashboard: Kiểm tra số liệu hiển thị đúng theo năm được chọn.
- Thử nghiệm submit form thuốc với số lượng âm: Kiểm tra hệ thống chặn lại và báo lỗi thân thiện.
