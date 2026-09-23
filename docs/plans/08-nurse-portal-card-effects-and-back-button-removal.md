# Kế Hoạch Loại Bỏ Nút Quay Lại & Tăng Cường Hiệu Ứng Màu Sắc Cho Khung Giao Diện Nurse Portal

> **Tài liệu tham khảo:** [master-upgrade-plan.md](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/docs/plans/master-upgrade-plan.md)  
> **Mã kế hoạch:** `08-nurse-portal-card-effects-and-back-button-removal`  
> **Trạng thái:** Chờ User phê duyệt (Pending Approval)  
> **Ngày lập:** 23/09/2026

---

## 1. Phân Tích Yêu Cầu & Hiện Trạng

### 1.1. Loại bỏ các nút "Quay lại" (Back Buttons Removal)
- **Lý do kỹ thuật & trải nghiệm:** 
  - Thanh điều hướng trên cùng (`Nav-Nurse`) đã có đầy đủ danh mục chức năng của Y tá (*Trang chủ, Tiêm chủng, Kiểm tra y tế, Y tế, Tạo lịch, Danh sách, Tư vấn, Hồ sơ cá nhân*).
  - Sự tồn tại của nút "Quay lại" trên các trang danh sách là dư thừa, chiếm diện tích thanh công cụ và làm giao diện bị rối.
- **Các trang danh sách cần loại bỏ nút "Quay lại":**
  1. `health-records/health_record_list.html` (Trang trong ảnh chụp của User)
  2. `vaccination-schedule-list.html` (Danh sách lịch tiêm chủng)
  3. `healthCheck-schedule-list.html` (Danh sách lịch khám sức khỏe)
  4. `medicalSupply/supply_list.html` (Danh sách vật tư y tế)
  5. `medicine/list.html` (Danh sách thuốc trong kho)
  6. `sent-medicine-usage/sent_medicine_list.html` (Danh sách thuốc phụ huynh đã gửi)
  7. `sent-medicine-usage/medicine_usage_history.html` (Lịch sử sử dụng thuốc)

---

### 1.2. Nâng Cấp Khung & Tăng Cường Hiệu Ứng Màu Sắc (Anti-Emptiness & Rich Aesthetics)
- **Vấn đề hiện tại:**
  - Khung nội dung hiện tại là một hình chữ nhật trắng đơn điệu, tiêu đề chỉ là dòng chữ `<h2>` nằm trơ trọi giữa khoảng trắng mênh mông, tạo cảm giác "đơn sắc và trống vắng".
  - Các ô dữ liệu trong bảng (như Dị ứng, Bệnh mãn tính, Chi tiết) là chữ đen đơn giản, nút "Xem chi tiết" là hình oval tròn to thô.
- **Mục tiêu nâng cấp:**
  - Tạo chiều sâu không gian (Depth) bằng hiệu ứng dải màu quang phổ (Mesh Gradient) ở nền.
  - Tạo điểm nhấn màu sắc nhận diện thương hiệu cho khung thẻ card (Gradient Top Accent Line & Icon Badge).
  - Biến phần tiêu đề trơ trọi thành **Header Banner chuyên nghiệp** có Icon huy hiệu, Tiêu đề chính và Phụ đề mô tả nghiệp vụ y tế.
  - Nâng cấp các ô dữ liệu trong bảng bằng các Tag Badge màu (ví dụ: tag Lớp nền xám nhẹ, tag "Không có" màu xanh lá dịu, tag "Có dị ứng" màu cam/đỏ cảnh báo dịu, nút "Xem chi tiết" dạng viên thuốc thanh mảnh có icon con mắt).

---

## 2. Các Phương Án Kỹ Thuật (Proactive Multi-Option Solutions)

Nhằm đảm bảo giao diện đạt chuẩn thẩm mỹ cao cấp, đề xuất 2 phương án thiết kế để User lựa chọn:

### 🌟 Phương Án 1 (Khuyến Nghị - Clinical Gradient Elevation & Executive Header Banner)
- **Nền trang (Mesh Ambient Glow):**
  - Sử dụng hiệu ứng nền Gradient Mesh mềm mại với các đốm sáng xanh y tế tỏa nhẹ:
    ```css
    background: radial-gradient(at 10% 15%, rgba(2, 132, 199, 0.08) 0px, transparent 45%),
                radial-gradient(at 90% 85%, rgba(14, 165, 233, 0.08) 0px, transparent 45%),
                radial-gradient(at 50% 50%, rgba(99, 102, 241, 0.04) 0px, transparent 60%),
                #f8fafc;
    ```
- **Viền Accent đa sắc trên đỉnh thẻ Card:**
  - Đỉnh của mỗi khung thẻ Card có dải màu quang phổ chuyển tiếp từ Xanh Dương -> Xanh Băng -> Tím Indigo (`linear-gradient(90deg, #0284c7 0%, #38bdf8 50%, #818cf8 100%)`, dày 4px).
- **Executive Header Banner (Loại bỏ cảm giác trống vắng):**
  - Thay vì chỉ có 1 dòng chữ `<h2>` thô sơ, tạo khối Header Banner sang trọng gồm:
    - **Icon Badge phát sáng nhẹ:** Hình huy hiệu y tế nằm trong ô vuông bo góc tròn (`width: 52px; height: 52px; background: linear-gradient(135deg, rgba(2, 132, 199, 0.12), rgba(14, 165, 233, 0.06)); border: 1px solid rgba(2, 132, 199, 0.25); border-radius: 14px`).
    - **Cụm tiêu đề & phụ đề:** Tiêu đề lớn in hoa đậm màu xanh `#0284c7`, bên dưới có dòng mô tả nghiệp vụ (VD: *"Quản lý và theo dõi thông tin tiền sử bệnh lý, dị ứng và thể lực học sinh toàn trường"*).
    - **Thanh công cụ Toolbar phía dưới:** Ô tìm kiếm được căn chỉnh cân đối bên trái, nút thao tác chính (Tạo mới, Thêm) bên phải.
- **Hiệu ứng Bảng & Tag dữ liệu:**
  - Đầu bảng dữ liệu có dải màu chuyển sắc sâu (`linear-gradient(135deg, #0284c7 0%, #0369a1 70%, #075985 100%)`).
  - Hàng trong bảng có hiệu ứng rê chuột mềm (`hover: background #f0f9ff; transition: all 0.2s ease`).
  - Cột "Chi tiết": Nút `.btn-action-view` dạng viên thuốc thanh lịch, nền xanh nhạt `#eff6ff`, viền `#bfdbfe`, chữ `#0284c7` kèm icon `<i class="fas fa-eye"></i>`.
- **Ưu điểm:** Cực kỳ chuyên nghiệp, sinh động, loại bỏ hoàn toàn sự trống trải, phong cách chuẩn các ứng dụng y tế/EHR quốc tế hàng đầu.

---

### 🎨 Phương Án 2 (Glassmorphism & Gradient Border)
- Khung Card sử dụng phong cách kính mờ (Backdrop-filter blur 16px) với viền Gradient phát sáng xung quanh toàn bộ khung.
- **Ưu điểm:** Hiện đại, lạ mắt, tạo cảm giác công nghệ cao.
- **Nhược điểm:** Hiệu ứng kính mờ có thể làm giảm độ tương phản văn bản nếu màn hình hiển thị của người dùng có độ sáng yếu.

> **👉 Đề xuất lựa chọn:** **Phương Án 1 (Clinical Gradient Elevation & Executive Header Banner)** vì vừa sống động, giàu hiệu ứng màu sắc vừa đảm bảo độ rõ nét, dễ quan sát dữ liệu y tế học sinh.

---

## 3. Danh Sách Các File Cần Chỉnh Sửa

### 3.1. Các Template HTML
1. `src/main/resources/templates/nurse/health-records/health_record_list.html`:
   - Xóa nút "Quay lại".
   - Tích hợp khối Executive Header Banner (Icon Badge `fa-notes-medical` + Tiêu đề + Phụ đề).
   - Tinh chỉnh nút "Xem chi tiết" thành viên thuốc thanh mảnh có icon mắt.
2. `src/main/resources/templates/nurse/vaccination-schedule-list.html`:
   - Xóa nút "Quay lại".
   - Bổ sung Header Banner (Icon Badge `fa-syringe`).
   - Căn chỉnh nút "Tạo lịch tiêm chủng" ở vị trí hài hòa trên toolbar.
3. `src/main/resources/templates/nurse/healthCheck-schedule-list.html`:
   - Xóa nút "Quay lại".
   - Bổ sung Header Banner (Icon Badge `fa-stethoscope`).
   - Căn chỉnh nút "Tạo lịch khám sức khỏe".
4. `src/main/resources/templates/nurse/medicalSupply/supply_list.html`:
   - Xóa nút "Quay lại".
   - Thanh công cụ cân đối: Ô tìm kiếm bên trái, nút "+ Thêm vật tư" bên phải.
5. `src/main/resources/templates/nurse/medicine/list.html`:
   - Xóa nút "Quay lại".
   - Ô tìm kiếm bên trái, nút "+ Thêm thuốc mới" bên phải.
6. `src/main/resources/templates/nurse/sent-medicine-usage/sent_medicine_list.html`:
   - Xóa nút "Quay lại", giữ nút "Lịch sử sử dụng thuốc" ở góc toolbar.

### 3.2. Các File Stylesheet CSS
1. `src/main/resources/static/assets/css/nurse/health-record/health_record_list.css`:
   - Bổ sung hiệu ứng Ambient Mesh Gradient cho `body`.
   - Bổ sung dải Accent Gradient Stripe 4px trên đỉnh card.
   - Thêm style cho `.header-banner`, `.header-icon-badge`, `.header-subtitle`.
   - Nâng cấp style cho `.btn-action-view` và các tag trạng thái dị ứng/bệnh.
2. `src/main/resources/static/assets/css/nurse/vaccination-schedule-list.css`:
   - Đồng bộ hiệu ứng Accent Gradient Stripe 4px, Header Banner và nút bấm.
3. `src/main/resources/static/assets/css/nurse/sent-medicine-usage/sent_medicine_list.css`:
   - Đồng bộ hiệu ứng Accent Gradient Stripe 4px và Header Banner.
4. `src/main/resources/static/assets/css/nurse/medicalSupply/supply_list.css`:
   - Đồng bộ hiệu ứng Accent Gradient Stripe 4px và Header Banner.
5. `src/main/resources/static/assets/css/nurse/medicine/list.css`:
   - Đồng bộ hiệu ứng Accent Gradient Stripe 4px và Header Banner.

---

## 4. Kế Hoạch Xác Minh & Kiểm Thử (Verification Plan)

1. **Biên dịch:** Chạy `.\mvnw.cmd test-compile` đảm bảo `BUILD SUCCESS` (0 lỗi).
2. **Kiểm tra trực quan bằng trình duyệt:**
   - Truy cập `/yte/nurse/health-records`: Kiểm tra không còn nút "Quay lại", khung có dải màu chuyển sắc, header có icon huy hiệu và phụ đề, bảng sinh động và nút xem chi tiết đẹp.
   - Truy cập `/yte/nurse/vaccination-schedule-list` & `/yte/nurse/healthCheck-schedule-list`: Kiểm tra bố cục cân đối không còn nút "Quay lại".
   - Truy cập `/yte/nurse/medicalSupply/supply_list`: Kiểm tra thanh công cụ tìm kiếm và thêm vật tư nằm đối xứng hài hòa.
3. Chụp ảnh màn hình minh chứng và cập nhật vào `walkthrough.md`.
