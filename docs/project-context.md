# 🏥 Phân Tích Bối Cảnh & Nghiệp Vụ Dự Án (Project Context & Domain Analysis)

Tài liệu này cung cấp toàn cảnh về bối cảnh nghiệp vụ, các chủ thể (actors), quy trình vận hành chi tiết và các ràng buộc kỹ thuật của hệ thống **School Medical Management System**. Mọi AI Agent và lập trình viên khi tham gia phát triển đều cần tham chiếu tài liệu này để hiểu rõ bản chất nghiệp vụ.

---

## 1. Bối Cảnh Nghiệp Vụ (Business Context)

Trong môi trường giáo dục phổ thông (Tiểu học, THCS, THPT), công tác y tế học đường đóng vai trò sống còn trong việc bảo vệ và chăm sóc sức khỏe ban đầu cho học sinh. Tuy nhiên, tại hầu hết các trường học truyền thống, việc quản lý y tế vẫn gặp nhiều bất cập:
* **Hồ sơ phân tán:** Sổ khám sức khỏe, phiếu tiêm chủng bằng giấy dễ thất lạc, khó tra cứu lịch sử bệnh nền của học sinh.
* **Tương tác chậm trễ giữa Phụ huynh và Y tá:** Khi học sinh bị ốm hoặc cần uống thuốc theo toa tại trường, phụ huynh thường chỉ dặn miệng hoặc viết giấy note sơ sài, dễ gây nhầm lẫn liều lượng.
* **Thiếu minh bạch khi xin ý kiến:** Việc gửi phiếu giấy xin ý kiến phụ huynh về khám sức khỏe định kỳ hoặc tiêm chủng vaccine có tỷ lệ phản hồi thấp, khó tổng hợp.
* **Khó khăn trong giám sát:** Ban giám hiệu và cơ quan y tế thiếu số liệu thống kê trực quan theo thời gian thực về tình hình dịch bệnh, tai nạn thương tích học đường.

**School Medical Management System** ra đời nhằm giải quyết triệt để các bài toán trên thông qua việc số hóa 100% quy trình y tế học đường.

---

## 2. Các Chủ Thể Tham Gia Hệ Thống (Stakeholders / Actors)

| Vai trò (Role) | Đối tượng thực tế | Trách nhiệm chính |
| :--- | :--- | :--- |
| **`ROLE_ADMIN`** | Quản trị viên hệ thống (IT School) | - Quản lý tài khoản người dùng, phân quyền RBAC.<br>- Giám sát hoạt động hệ thống, cấu hình tham số.<br>- Xem dashboard thống kê tổng hợp toàn trường. |
| **`ROLE_NURSE`** | Cán bộ Y tế / Y tá học đường | - Lên lịch và gửi phiếu đồng ý khám sức khỏe & tiêm chủng.<br>- Nhập và theo dõi kết quả khám sức khỏe, kết quả sau tiêm.<br>- Xử lý sơ cấp cứu tai nạn/sự cố y tế học đường (`MedicalEvent`).<br>- Quản lý tủ thuốc, vật tư y tế tiêu hao.<br>- Tiếp nhận thuốc gửi từ phụ huynh và ghi nhật ký cho học sinh uống thuốc.<br>- Tiếp nhận và thực hiện lịch hẹn tư vấn sức khỏe. |
| **`ROLE_PARENT`** | Phụ huynh học sinh | - Khai báo và theo dõi hồ sơ sức khỏe (`HealthRecord`) của con.<br>- Nhận thông báo và phê duyệt/từ chối phiếu khám sức khỏe, tiêm chủng.<br>- Gửi thuốc và hướng dẫn sử dụng thuốc cho con tại trường.<br>- Đặt lịch hẹn tư vấn y tế với nhân viên y tế nhà trường. |
| **`ROLE_MANAGER`** | Ban Giám hiệu / Cán bộ Quản lý | - Xem danh sách học sinh theo khối/lớp.<br>- Theo dõi tổng quan các chỉ số sức khỏe học đường, tỷ lệ tiêm chủng. |
| **Học sinh (Student)** | Học sinh trong trường | - Đối tượng trung tâm được chăm sóc y tế (không trực tiếp tương tác với phần mềm; dữ liệu được quản lý thông qua Phụ huynh và Nhà trường). |

---

## 3. Các Quy Trình Nghiệp Vụ Cốt Lõi (Core Business Workflows)

### 3.1. Quy trình Quản lý Tài khoản & Xác thực (Authentication & User Lifecycle)
1. **Đăng ký / Tạo tài khoản:**
   - Admin có thể tạo tài khoản cho Quản lý, Y tá, Phụ huynh.
   - Khi tạo tài khoản người dùng (`User`), hệ thống tự động sinh bản ghi thực thể tương ứng (`Admin`, `SchoolNurse`, `Parent`, `Manager`) theo quan hệ 1-1.
2. **Đăng nhập & Phân luồng:**
   - Admin đăng nhập tại `/yte/admin/login` -> chuyển hướng đến `/yte/admin/dashboard`.
   - Các vai trò khác đăng nhập tại `/yte/login` -> `CustomSuccessHandler` kiểm tra Role và điều hướng:
     - `ROLE_NURSE` -> `/yte/nurse/nurse-home`
     - `ROLE_PARENT` -> `/yte/parent/parent-home`
     - `ROLE_MANAGER` -> `/yte/manager/manager-home`
3. **Quên mật khẩu & Đặt lại mật khẩu (OTP Recovery):**
   - Người dùng nhập Email -> Hệ thống sinh mã OTP ngẫu nhiên (hiệu lực 1-5 phút) -> Gửi qua email bằng SMTP Gmail.
   - Người dùng xác thực mã OTP -> Nếu hợp lệ, chuyển sang trang đặt lại mật khẩu mới.

---

### 3.2. Quy trình Khám Sức Khỏe Định Kỳ (Periodic Health Check Workflow)
```
[Y tá] Lập lịch khám theo khối/lớp
       │
       ▼
[Hệ thống] Tạo các bản ghi HealthCheckConsent (trạng thái: UNCONFIRMED)
       │
       ▼
[Phụ huynh] Nhận thông báo -> Chọn Đồng ý (ACCEPTED) hoặc Từ chối (DECLINED)
       │
       ├──────────── Nếu DECLINED / Hết hạn: Kết thúc quy trình với HS này
       ▼ Nếu ACCEPTED
[Y tá] Tiến hành khám tại trường -> Ghi nhận HealthCheckRecord
       (thị lực, thính lực, huyết áp, nhịp tim, chiều cao, cân nặng, kết luận)
       │
       ▼
[Y tá] Nếu phát hiện bất thường -> Đánh dấu needs_consultation = true
       │
       ▼
[Phụ huynh] Xem kết quả khám chi tiết của con trên giao diện
```

---

### 3.3. Quy trình Chiến Dịch Tiêm Chủng (Vaccination Campaign Workflow)
1. **Y tá tạo lịch tiêm chủng (`VaccinationSchedule`):** Chọn loại vaccine, độ tuổi khuyến nghị, ngày tiêm, gửi thông báo đến phụ huynh.
2. **Hệ thống phát sinh phiếu lấy ý kiến (`VaccinationConsent`):** Gửi tới từng phụ huynh có con trong danh sách.
3. **Phụ huynh xác nhận:** Phụ huynh gửi phản hồi trực tuyến (Đồng ý/Từ chối tiêm).
4. **Y tá thực hiện tiêm & theo dõi sau tiêm:**
   - Y tá kiểm tra danh sách học sinh đã được duyệt tiêm.
   - Sau khi tiêm, y tá ghi nhận kết quả (`VaccinationRecord`) bao gồm: tình trạng sức khỏe sau tiêm (bình thường, sốt nhẹ, dị ứng,...), thời gian theo dõi, ghi chú.
   - Kết quả được gửi về tài khoản phụ huynh để theo dõi tại nhà.

---

### 3.4. Quy trình Xử Lý Sự Cố Y Tế & Cấp Cứu Học Đường (Medical Event Workflow)
1. **Phát sinh sự cố:** Học sinh bị chấn thương thể thao, đau bụng, sốt cao, ngất xỉu,... được đưa vào phòng y tế.
2. **Y tá lập biên bản sự cố (`MedicalEvent`):**
   - Chọn học sinh, ghi nhận thời gian, địa điểm, mô tả triệu chứng ban đầu.
   - Ghi nhận phương án sơ cứu ban đầu (`initial_treatment`).
3. **Kê đơn thuốc & Vật tư tiêu hao tại chỗ:**
   - Y tá chọn thuốc từ kho (`MedicineUsed`) và vật tư đã dùng như bông băng, cồn (`SupplyUsed`).
   - Hệ thống tự động trừ số lượng tồn kho tương ứng trong bảng `medicine` và `medical_supplies`.
4. **Xử lý dứt điểm (`final_treatment`):** Cho về lớp tiếp tục học, hoặc liên hệ phụ huynh đón về, hoặc chuyển viện cấp cứu.

---

### 3.5. Quy trình Tiếp Nhận Thuốc Phụ Huynh Gửi (Sent Medicine Workflow)
1. **Phụ huynh gửi thuốc (`SentMedicine`):**
   - Phụ huynh khai báo danh mục thuốc con cần uống tại trường, liều lượng, giờ uống và hướng dẫn chi tiết.
2. **Y tá tiếp nhận & cho học sinh uống thuốc:**
   - Y tá nhận thuốc vật lý tại phòng y tế đối chiếu với đơn trên hệ thống.
   - Đến giờ uống, y tá cho học sinh uống và tạo bản ghi nhật ký sử dụng (`SentMedicineUsage`): ghi rõ thời gian thực tế, liều lượng đã uống, biểu hiện của học sinh.
   - Phụ huynh có thể tra cứu tức thì xem con mình đã được cho uống thuốc lúc mấy giờ.

---

### 3.6. Quy trình Hẹn Tư Vấn Sức Khỏe (Consultation Appointment Workflow)
1. **Khởi tạo lịch hẹn:**
   - Phát sinh từ kết luận khám sức khỏe có bất thường (`needs_consultation = true`), hoặc do phụ huynh chủ động yêu cầu.
2. **Xác nhận lịch hẹn:**
   - Y tá sắp xếp thời gian (`scheduled_time`), nội dung tư vấn và gửi xác nhận cho phụ huynh.
   - Cả hai bên theo dõi trạng thái lịch hẹn (`UNCONFIRMED`, `ACCEPTED`, `DECLINED`, `OVERDUE`).

---

## 4. Các Ràng Buộc Kỹ Thuật & Nghiệp Vụ (Constraints & Business Rules)

1. **Ràng buộc quan hệ dữ liệu (Data Integrity):**
   - Một phụ huynh có thể có nhiều con (`1 Parent -> N Students`), nhưng một học sinh trong mô hình hiện tại thuộc về một phụ huynh đại diện.
   - Mỗi học sinh có duy nhất một Hồ sơ sức khỏe tổng quát (`HealthRecord`).
   - Hồ sơ khám sức khỏe (`HealthCheckRecord`) gắn liền 1-1 với phiếu đồng ý (`HealthCheckConsent`).
2. **Bảo mật & Phân quyền (Security Invariants):**
   - Phụ huynh CHỈ ĐƯỢC PHÉP xem và thao tác trên dữ liệu học sinh là con ruột của mình (kiểm tra `parent_id` khớp với `current_user`).
   - Mật khẩu người dùng bắt buộc phải được mã hóa bằng thuật toán **BCrypt** trước khi lưu vào CSDL.
   - Mọi thao tác thay đổi dữ liệu hoặc xóa tài nguyên phải thực hiện qua phương thức `POST`/`DELETE` có xác thực CSRF Token.
3. **Hiệu năng & Toàn vẹn giao dịch (Transactions):**
   - Khi gửi lịch khám/tiêm chủng cho toàn khối học sinh (hàng trăm em), bắt buộc phải thực hiện trong một Transaction (`@Transactional`) và dùng `saveAll()` để tối ưu batch insert.
