# 📖 Cẩm Nang Hướng Dẫn Sử Dụng (System User Guide)

Tài liệu hướng dẫn sử dụng toàn diện dành cho tất cả các đối tượng người dùng trong hệ thống **Quản Lý Y Tế Học Đường (School Medical Management System)**: **Quản Trị Viên (Admin)**, **Quản Lý Học Sinh (Manager)**, **Y Tá Học Đường (Nurse)** và **Phụ Huynh Học Sinh (Parent)**.

---

## 🔑 1. Đăng Nhập & Truy Cập Hệ Thống

| Vai trò | Cổng đăng nhập | Định dạng tài khoản | Mật khẩu mặc định |
| :--- | :--- | :--- | :--- |
| **Admin** | `http://<domain>/yte/admin/login` | Tên đăng nhập (`admin`) | `123456` |
| **Manager** | `http://<domain>/yte/login` | Số điện thoại 10 số (`0988112233`) | `123456` |
| **Nurse** | `http://<domain>/yte/login` | Số điện thoại 10 số (`0977112234`) | `123456` |
| **Parent** | `http://<domain>/yte/login` | Số điện thoại 10 số (`0912345671`) | `123456` |

> [!NOTE]
> Để tăng tính an toàn, tất cả người dùng thuộc nhóm User (Manager, Nurse, Parent) đăng nhập trực tiếp bằng **Số điện thoại cá nhân**. Riêng Quản trị viên sử dụng cổng đăng nhập an ninh bảo mật riêng biệt tại `/admin/login`.

---

## 🛡️ 2. Hướng Dẫn Dành Cho Quản Trị Viên (Admin)

### 2.1. Bảng điều khiển trung tâm (Dashboard KPI)
- **Truy cập:** Menu `Dashboard` (`/admin/dashboard`).
- **Chức năng:**
  - Theo dõi 4 chỉ số KPI quan trọng: Tổng số tài khoản, Số học sinh trong trường, Số lượt khám sức khỏe đã thực hiện, Tổng số đợt tiêm chủng.
  - Bộ lọc thống kê linh hoạt theo từng năm học (2024, 2025, 2026...).
  - Biểu đồ trực quan hóa dữ liệu y tế học đường.

### 2.2. Quản lý tài khoản người dùng
- **Truy cập:** Menu `Quản lý người dùng` (`/admin/manage-users`).
- **Thao tác:**
  - **Thêm người dùng mới:** Nhấn nút `+ Thêm người dùng`, điền Tên đăng nhập, Email, Mật khẩu và chỉ định vai trò (`ROLE_MANAGER`, `ROLE_NURSE`, `ROLE_PARENT`, `ROLE_ADMIN`).
  - **Cập nhật thông tin:** Xem danh sách, nhấn icon `Chỉnh sửa` tại từng hàng. Hệ thống tự động kiểm tra trùng lặp email/username và hỗ trợ đổi mật khẩu nhanh.
  - **Xóa / Khóa tài khoản:** Xóa tài khoản khi nhân viên nghỉ việc hoặc phụ huynh chuyển trường cho con.

---

## 🎓 3. Hướng Dẫn Dành Cho Quản Lý Học Sinh (Manager)

### 3.1. Trang chủ Quản lý (Manager Home)
- **Truy cập:** `/manager/home`.
- **Chức năng:**
  - Xem tổng số học sinh toàn trường, phân bổ học sinh theo khối lớp, danh sách học sinh mới nhập học trong tháng.
  - Phím tắt điều hướng nhanh đến danh sách lớp và tạo hồ sơ học sinh.

### 3.2. Quản lý hồ sơ học sinh (Student CRUD)
- **Truy cập:** Menu `Danh sách học sinh` (`/manager/students/list`).
- **Thao tác:**
  - **Thêm học sinh mới:** Nhấn nút `+ Thêm học sinh`, nhập đầy đủ: Họ và tên (tự động chuẩn hóa Unicode, loại bỏ ký tự lạ), Giới tính, Ngày sinh, Lớp học, Địa chỉ và chọn Phụ huynh liên kết.
  - **Chỉnh sửa học sinh:** Cập nhật lại thông tin lớp học khi học sinh lên lớp mới.
  - **Xóa học sinh:** Hệ thống tự động kiểm tra ràng buộc toàn vẹn; nếu học sinh đang có hồ sơ sức khỏe hoặc kết quả khám/tiêm chủng liên quan, hệ thống sẽ cảnh báo an toàn để tránh mất dữ liệu y tế.

---

## 🩺 4. Hướng Dẫn Dành Cho Y Tá Học Đường (School Nurse)

### 4.1. Quản lý Hồ sơ Sức khỏe Học sinh
- **Truy cập:** Menu `Hồ sơ sức khỏe` (`/nurse/health-record`).
- **Thao tác:**
  - Tìm kiếm học sinh theo họ tên hoặc lọc theo danh sách lớp.
  - Xem chi tiết hồ sơ: Tiền sử dị ứng thuốc/thức ăn, bệnh mãn tính, thị lực, thính giác.
  - **Xuất Thẻ Y Tế (PDF):** Nhấn nút `📥 Tải Thẻ Y Tế (PDF)` để tải file PDF in ấn chuẩn phôi y tế học đường lưu hồ sơ văn phòng.

### 4.2. Lập lịch Khám sức khỏe & Tiêm chủng
- **Lập lịch khám:** Vào menu `Lập lịch khám` (`/nurse/healthCheck-schedule/create`), chọn ngày khám, khối lớp và gửi phiếu lấy ý kiến đến phụ huynh.
- **Lập lịch tiêm:** Vào menu `Lập lịch tiêm` (`/nurse/vaccination-schedule/create`), chọn loại vắc-xin, liều lượng, ngày tiêm và gửi thông báo.
- **Xuất báo cáo Excel:** Tại màn hình danh sách lịch đã gửi, nhấn nút **"📊 Xuất Excel"** để tải file `.xlsx` tổng hợp đầy đủ chữ ký y tá và danh sách học sinh.

### 4.3. Quản lý Thuốc gửi & Cho học sinh uống thuốc
- **Truy cập:** Menu `Thuốc phụ huynh gửi` (`/nurse/sent-medicine/list`).
- **Thao tác:**
  - Tiếp nhận đơn thuốc phụ huynh gửi (tên thuốc, liều lượng, giờ uống).
  - Nhấn `Ghi nhận cho uống thuốc`: Nhập thời gian thực tế cho học sinh uống thuốc, ghi chú phản ứng sau khi uống (hạ sốt, bình thường...).
  - Xem lịch sử uống thuốc chi tiết của từng học sinh.

### 4.4. Quản lý Tủ thuốc & Vật tư Y tế
- **Truy cập:** Menu `Tủ thuốc` (`/nurse/medicine`) và `Vật tư y tế` (`/nurse/medical-supply`).
- **Thao tác:** Theo dõi số lượng tồn kho, cảnh báo thuốc sắp hết hạn hoặc dưới mức tối thiểu, nhập thêm thuốc và ghi nhận hao phí khi xử lý sơ cấp cứu tai nạn học đường.

---

## 👨‍👩‍👧 5. Hướng Dẫn Dành Cho Phụ Huynh Học Sinh (Parent)

### 5.1. Xem & Quản lý Thẻ Y Tế Điện Tử
- **Truy cập:** Menu `Hồ sơ sức khỏe` (`/parent/health-record/select-student`).
- **Thao tác:**
  - Chọn học sinh cần xem (nếu phụ huynh có nhiều con cùng học tại trường).
  - Khai báo / Cập nhật tiền sử dị ứng, bệnh mãn tính để nhà trường chủ động chăm sóc.
  - Nhấn **"📥 Tải Thẻ Y Tế (PDF)"** để tải Thẻ y tế điện tử chính thức về điện thoại hoặc máy tính.

### 5.2. Gửi thuốc nhờ Y tá cho con uống tại trường
- **Truy cập:** Menu `Gửi thuốc` (`/parent/sent-medicine/list`).
- **Thao tác:**
  - Nhấn nút `+ Gửi thuốc mới`.
  - Nhập tên thuốc, liều lượng, hướng dẫn uống (ví dụ: *1 gói hạ sốt sau ăn trưa, siro ho 5ml lúc 14h*).
  - Theo dõi nhật ký thời gian thực xem y tá đã cho con uống thuốc vào lúc mấy giờ kèm ghi chú chăm sóc.

### 5.3. Xác nhận Phiếu Khám Sức Khỏe & Tiêm Chủng
- **Truy cập:** Menu `Khám sức khỏe` hoặc `Tiêm chủng`.
- **Thao tác:**
  - Khi có đợt khám/tiêm mới, chuông thông báo trên Header sẽ hiển thị chấm đỏ và số lượng thông báo mới.
  - Mở chi tiết phiếu khám/tiêm: Xem thông tin bác sĩ, ngày khám, loại vắc-xin.
  - Chọn **Đồng ý** hoặc **Từ chối** kèm lý do gửi trực tiếp đến y tá phụ trách.

### 5.4. Đổi Mật Khẩu & Bảo Mật Tài Khoản
- Nhấp vào avatar cá nhân ở góc trên bên phải $\rightarrow$ Chọn `Thông tin cá nhân` hoặc `Đổi mật khẩu`.
- Hỗ trợ tính năng `Quên mật khẩu`: Nhận mã OTP xác thực qua email để đặt lại mật khẩu an toàn khi bị mất.
