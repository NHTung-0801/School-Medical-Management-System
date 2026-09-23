# Kế Hoạch Đồng Bộ Giao Diện Nurse Portal, Khắc Phục Lỗi Lơ Lửng Footer & Loại Bỏ Tông Màu Đen

> **Tài liệu tham khảo:** [master-upgrade-plan.md](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/docs/plans/master-upgrade-plan.md)  
> **Mã kế hoạch:** `07-nurse-portal-ui-redesign`  
> **Trạng thái:** Chờ User phê duyệt (Pending Approval)  
> **Ngày lập:** 23/09/2026

---

## 1. Phân Tích Hiện Trạng & Nguyên Nhân Gốc Rễ (Root Causes)

Dựa trên 3 ảnh chụp thực tế từ màn hình của Y tá (Nurse), hệ thống xác định 3 vấn đề trọng tâm:

### 1.1. Lỗi Lơ Lửng Footer (Floating Footer Bug - Ảnh 1 & Ảnh 3)
- **Biểu hiện:** Phía dưới footer xuất hiện một khoảng trống (moat/dải màu nền xanh gradient `20px` - `40px`), footer không bám sát cạnh đáy màn hình; đồng thời ở các bảng dữ liệu ít (như Danh sách tiêm chủng rỗng), khoảng trống giữa bảng và footer bị kéo dài quá mức một cách kỳ dị.
- **Nguyên nhân cốt lõi:**
  1. Trong hầu hết các file CSS của Nurse (`vaccination-schedule-list.css`, `sent_medicine_list.css`, `supply_list.css`, `medicine/list.css`,...):
     ```css
     body {
         padding: 20px; /* hoặc padding: 40px */
         min-height: 100vh;
     }
     ```
     Thuộc tính `padding: 20px/40px` áp dụng trực tiếp lên thẻ `body` khiến toàn bộ trang web (bao gồm `<header>` và `<footer>`) bị đẩy lùi vào trong một khoảng đệm 20-40px, tạo ra viền lơ lửng bên dưới footer.
  2. Trong file `templates/nurse/vaccination-schedule-list.html` (dòng 19):
     ```html
     <div class="bg-pink min-vh-100 d-flex justify-content-center align-items-start py-5">
     ```
     Class `min-vh-100` của Bootstrap ép khung chứa nội dung phải cao tối thiểu 100% chiều cao màn hình (`100vh`), cộng với chiều cao của Header (~120px) và Footer (~50px), khiến trang luôn bị đội lên `100vh + 170px` ngay cả khi chỉ có 0-1 dòng dữ liệu, tạo ra khoảng trống trắng ngút ngàn.

---

### 1.2. Lỗi Tông Màu Đen (Pitch-Black Dark Mode Bug - Ảnh 2)
- **Biểu hiện:** Trang **"Danh Sách Lịch Khám Sức Khỏe"** (`/nurse/healthCheck-schedule-list`) có toàn bộ nền màu đen tuyền kịt (`#0f0e17`), bảng tương phản gắt, chữ trắng trên nền sáng gây mất thẩm mỹ trầm trọng và lệch hoàn toàn so với phong cách trang y tế học đường.
- **Nguyên nhân cốt lõi:**
  - Trong `src/main/resources/templates/nurse/healthCheck-schedule-list.html` dòng 15:
    ```html
    <link rel="stylesheet" th:href="@{/assets/css/index.css}" />
    ```
  - `index.css` là stylesheet của trang chủ Public Dark Mode (`body { background: #0f0e17; color: #e2e8f0; }`) có kèm lớp phủ noise texture overlay `body::after`. Việc nạp nhầm `index.css` vào trang quản lý của Y tá đã biến trang thành nền đen.

---

### 1.3. Bố Cục & Các Nút Chưa Đồng Điệu (Inconsistent Layout & Buttons)
- **Biểu hiện:**
  - **Nút "Quay lại":**
    - Trang 1 (`vaccination-schedule-list.html`): Dùng `.btn-gray` viền mảnh hình chữ nhật, mũi tên thô `← Quay lại`.
    - Trang 2 (`healthCheck-schedule-list.html`): Dùng `<button class="btn-secondary">` màu xám xịt khối hộp `#757575`.
    - Trang 3 (`supply_list.html`): Dùng `.btn-back` bo tròn hình viên thuốc (pill 25px) có icon FontAwesome.
  - **Nút chính (Tạo lịch / Thêm mới):**
    - Trang 1: Bên phải, bo góc 6px, màu xanh phẳng.
    - Trang 2: Bên trái, màu xanh, bo góc vuông vức.
    - Trang 3: Bên phải, bo tròn viên thuốc 25px có icon cộng.
  - **Nút thao tác trong bảng (Actions):**
    - "Gửi xác nhận" trang 1 là nút viền outline pill, trang 2 là nút xanh vuông vức.
    - "Sửa / Xóa" trang 3 là các viên thuốc nhỏ outline xanh/đỏ.
  - **Khung chứa (Card/Container):**
    - Trang 1: Bọc trong `.schedule-card` bo góc có bóng đổ nhưng bị bao bởi `min-vh-100`.
    - Trang 2 & 3: Bảng trôi nổi tự do không nằm trong card trắng chuẩn mực.

---

## 2. Đề Xuất Các Phương Án Kỹ Thuật (Proactive Multi-Option Solutions)

Nhằm đảm bảo giao diện đạt chuẩn thẩm mỹ cao cấp, đồng bộ và đúng ý đồ của User, đề xuất 2 phương án thiết kế để User lựa chọn:

### 🌟 Phương Án A (Khuyến Nghị - Standardized Medical Clean Theme)
- **Khung nền & Thẻ Card:**
  - Nền trang web: Gam màu trung tính sáng `#f8fafc` hoặc gradient xanh băng siêu nhẹ `linear-gradient(180deg, #f0f9ff 0%, #f8fafc 100%)`.
  - Khung nội dung (`.medical-card` / `.schedule-card`): Thẻ card trắng nổi (`background: #ffffff`, bo góc `14px`, viền `1px solid #e2e8f0`, đổ bóng mềm `0 4px 20px rgba(15, 23, 42, 0.05)`, padding `28px 32px`, max-width `1240px`).
- **Hệ thống nút bấm chuẩn mực (Đồng điệu với Manager & Parent):**
  - **Nút "Quay lại" (`.btn-back`):** Nền trắng `#ffffff`, viền xám nhạt `#cbd5e1`, chữ `#475569`, bo góc `8px`, icon `<i class="fas fa-arrow-left"></i>`, hover chuyển `#f1f5f9`.
  - **Nút chính "Tạo lịch / Thêm mới" (`.btn-add` / `.btn-primary`):** Xanh y tế `linear-gradient(135deg, #0284c7, #0ea5e9)`, chữ trắng, bo góc `8px`, icon phù hợp (`fa-plus`, `fa-calendar-plus`), bóng đổ `0 4px 12px rgba(2, 132, 199, 0.25)`.
  - **Nút thao tác dòng (Table Actions):**
    - *"Gửi xác nhận":* Nút xanh đậm y tế `background: #0284c7`, bo góc `6px`, chữ trắng, padding `6px 14px`, font `13px` gọn gàng.
    - *"Sửa":* Badge xanh nhạt mềm `background: #eff6ff`, viền `#bfdbfe`, chữ `#0284c7`, icon `<i class="fas fa-pen"></i>`.
    - *"Xóa":* Badge đỏ nhạt mềm `background: #fef2f2`, viền `#fecaca`, chữ `#dc2626`, icon `<i class="fas fa-trash-alt"></i>`.
- **Đầu bảng dữ liệu (Table Header):** Màu xanh Deep Sky Blue `#0284c7` với chữ trắng nổi bật, góc trên bo tròn bo theo card.
- **Ưu điểm:** Giao diện sắc nét, phong cách chuyên nghiệp chuẩn y tế (Medical UI), giải quyết dứt điểm sự lộn xộn giữa các trang.
- **Đánh giá rủi ro:** 0 rủi ro backend vì giữ nguyên 100% Thymeleaf bindings, IDs, form actions.

---

### 🎨 Phương Án B (Executive Slate Theme - Giống 100% Manager Student List)
- Tương tự Phương án A về cấu trúc card và nút bấm, nhưng Table Header sử dụng tông xanh băng nhạt:
  - `thead th`: Nền `#e0f2fe`, chữ xanh đậm `#0369a1`, chữ in hoa cỡ nhỏ `13px`, viền dưới `2px solid #bae6fd`.
- **Ưu điểm:** Đồng bộ 1:1 tuyệt đối với bảng `manager/student_list.html`.
- **Nhược điểm:** Tông bảng sáng đồng đều, độ tương phản tiêu đề bảng thấp hơn Phương án A một chút đối với màn hình y tế.

> **👉 Đề xuất lựa chọn:** **Phương Án A (Vibrant Medical Sky Blue Header + Clean White Elevation Card)** vì mang lại diện mạo hiện đại, rõ ràng, dễ phân biệt nghiệp vụ khám/tiêm chủng của Y tá.

---

## 3. Danh Sách Các File & Hạng Mục Cần Can Thiệp

### Nhóm 1: Sửa Lỗi Tông Màu Đen & Khắc Phục Lỗi Footer
1. `src/main/resources/templates/nurse/healthCheck-schedule-list.html`:
   - Xóa bỏ dòng 15: `<link rel="stylesheet" th:href="@{/assets/css/index.css}" />`.
   - Chuẩn hóa cấu trúc nút bấm (thay thẻ lồng `<a...><button...>` sai chuẩn HTML5 thành thẻ `<a>` chuẩn CSS class).
2. `src/main/resources/templates/nurse/vaccination-schedule-list.html`:
   - Xóa bỏ class `min-vh-100` và `bg-pink` gây khoảng trống khổng lồ.
   - Chuẩn hóa thanh công cụ toolbar và các nút thao tác.
3. `src/main/resources/templates/nurse/ListSentVaccinationSchedules.html`:
   - Xóa bỏ thuộc tính inline `style="background-color: #fff0f5;"`.

### Nhóm 2: Xử Lý Triệt Để Lơ Lửng Footer Trên Toàn Bộ CSS Nurse
- Xóa bỏ hoàn toàn `padding: 20px;` và `padding: 40px;` khỏi selector `body` trong tất cả các file CSS của Nurse:
  1. `assets/css/nurse/vaccination-schedule-list.css`
  2. `assets/css/nurse/sent-medicine-usage/sent_medicine_list.css`
  3. `assets/css/nurse/medicalSupply/supply_list.css`
  4. `assets/css/nurse/medicine/list.css`
  5. `assets/css/nurse/list-sent-vaccination.css`
  6. `assets/css/nurse/sent-medicine-usage/medicine_usage_history.css`
  7. `assets/css/nurse/sent-medicine-usage/sent_medicine_usage_form.css`
  8. `assets/css/nurse/vaccination-schedule-form.css`
  9. `assets/css/nurse/vaccination-record-form.css`
  10. `assets/css/nurse/vaccination-list.css`
  11. `assets/css/nurse/medicalSupply/supply_form.css`
  12. `assets/css/nurse/medicine/form.css`
  13. `assets/css/nurse/health-record/health_record_list.css`
  14. `assets/css/nurse/health-record/health_record_view.css`
  15. `assets/css/nurse/Result.css`
  16. `assets/css/nurse/list-student-vaccination.css`
  17. `assets/css/nurse/list-student-health-checked.css`
  18. `assets/css/nurse/HSCheckLichKham.css`
  19. `assets/css/nurse/LichKhamDaGui.css`
  20. `assets/css/nurse/heath-chekup-calendar.css`
  21. `assets/css/nurse/listCreatedReview.css`
  22. `assets/css/nurse/createReview.css`

- Thiết lập cấu trúc Flexbox Sticky chuẩn trong CSS:
  ```css
  body {
      background-color: #f8fafc;
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      margin: 0;
      padding: 0;
  }
  .container,
  .schedule-page-wrapper,
  .medical-card-wrapper {
      flex: 1 0 auto;
      width: 100%;
  }
  ```

### Nhóm 3: Tinh Chỉnh Bố Cục & Thiết Kế Các Nút Đồng Điệu
- Cập nhật giao diện thanh công cụ (Toolbar) và hệ thống nút cho 4 màn hình chính:
  1. `vaccination-schedule-list.html` + `vaccination-schedule-list.css` (Ảnh 1)
  2. `healthCheck-schedule-list.html` + `sent_medicine_list.css` (Ảnh 2)
  3. `medicalSupply/supply_list.html` + `medicalSupply/supply_list.css` (Ảnh 3)
  4. `medicine/list.html` + `medicine/list.css`

---

## 4. Kế Hoạch Xác Minh & Kiểm Thử (Verification Plan)

1. **Kiểm tra biên dịch:**
   - Chạy lệnh `.\mvnw.cmd test-compile` để đảm bảo 0 lỗi biên dịch, không ảnh hưởng đến bất kỳ controller hay model nào.
2. **Kiểm tra trực quan trên trình duyệt (Browser Subagent):**
   - Đăng nhập tài khoản Nurse (`0977112234` / `123456`).
   - Truy cập kiểm tra từng trang:
     - `/yte/nurse/vaccination-schedule-list` (Xác minh không còn khoảng trống lớn, footer nằm sát đáy màn hình, nút "Quay lại" và "Tạo lịch tiêm chủng" đồng điệu).
     - `/yte/nurse/healthCheck-schedule-list` (Xác minh hết tông đen 100%, nền sáng y tế dịu mắt, các nút cân đối).
     - `/yte/nurse/medicalSupply/supply_list` (Xác minh footer sát đáy, nút quay lại, ô tìm kiếm và nút thêm mới đồng bộ kích thước và bo góc).
3. **Báo cáo kết quả:**
   - Chụp ảnh màn hình minh chứng và cập nhật vào `walkthrough.md`.
