# 🎨 Kế Hoạch Nâng Cấp Giao Diện Trang Chủ: Hiệu Ứng Cuộn Cuộn (Scroll Reveal) & Không Gian 3D (3D Depth & Tilt)

> **Mục tiêu:** Nâng tầm trải nghiệm thị giác cho trang chủ `index.html` của hệ thống **School Medical Management System**, bổ sung hiệu ứng cuộn đến đâu nội dung hiển thị đến đó (Scroll-driven Reveal) và hiệu ứng chiều sâu 3D tương tác (3D Card Tilt, Parallax, Floating Elements) theo tiêu chuẩn web hiện đại cao cấp (Modern Clinical Aesthetics).

---

## 🔍 1. Phân Tích Hiện Trạng & Yêu Cầu

### Hiện trạng:
* Trang chủ [index.html](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/resources/templates/user/index.html) đã có giao diện nền tối hiện đại (Dark Glassmorphism), bố cục gồm:
  - Hero Section (Banner chính, khẩu hiệu, nút lối tắt vai trò, trust badges)
  - Dịch vụ y tế trọng tâm (4 Service Cards)
  - Về hệ thống (3 Value Cards)
  - Tài liệu sức khỏe học đường (Accordion)
  - Thống kê nhanh (3 Stat Cards)
  - Liên hệ & Hỗ trợ y tế (Contact Info & Quick Form)
* Hiện tại các thành phần hiển thị tĩnh, chưa có hiệu ứng xuất hiện động theo thanh cuộn và chưa có chiều sâu 3D tương tác khi di chuột.

### Mục tiêu kỹ thuật cần đạt:
1. **Hiệu ứng Scroll Reveal (Lướt đến đâu nội dung đi đến đó):**
   - Khi người dùng cuộn chuột xuống, từng section, khối tiêu đề và card nội dung sẽ xuất hiện mượt mà (Fade In Up, 3D Flip/Slide) với độ trễ so le (Staggered Animation 100ms - 150ms).
   - Tự động kích hoạt thông qua `IntersectionObserver` tối ưu hiệu năng 60-120 FPS, không gây giật lag.
2. **Hiệu ứng 3D Tương Tác (3D Depth, Card Tilt & Parallax):**
   - **3D Card Hover Tilt:** Khi rê chuột qua các thẻ dịch vụ, thẻ giá trị và thẻ thống kê, thẻ sẽ nghiêng 3D đa hướng theo góc chuột (`perspective`, `rotateX`, `rotateY`, `translateZ(15px)`), kết hợp hiệu ứng phản chiếu ánh sáng (Specular Glare Overlay).
   - **3D Floating Elements:** Các biểu tượng y tế (icon stethoscope, heart, shield, pills) có hiệu ứng bồng bềnh 3D đa chiều.
   - **Parallax Scroll:** Hero background và các khối card chuyển động theo độ sâu thị giác khác nhau khi cuộn trang.

---

## 🛠️ 2. Các File Bị Ảnh Hưởng
1. [src/main/resources/static/assets/css/index.css](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/resources/static/assets/css/index.css): Bổ sung CSS 3D perspective, keyframe animations, trạng thái scroll reveal (`.reveal-hidden`, `.revealed`), và hiệu ứng 3D hover.
2. [src/main/resources/static/assets/js/index.js](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/resources/static/assets/js/index.js): Bổ sung logic `IntersectionObserver` cho Scroll Reveal và hàm tính toán góc nghiêng 3D đa điểm (Vanilla 3D Card Tilt) nhẹ nhàng ~2KB.
3. [src/main/resources/templates/user/index.html](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/resources/templates/user/index.html): Gắn các class/attribute điều khiển hiệu ứng vào các phần tử mục tiêu.

---

## 📊 3. Đánh Giá Các Phương Án Kỹ Thuật

| Tiêu chí | Phương án 1: Thuần Vanilla JS + CSS3 3D (Khuyến nghị) | Phương án 2: Thư viện ngoài AOS + Vanilla-Tilt CDN | Phương án 3: 3D Canvas Three.js |
| :--- | :--- | :--- | :--- |
| **Hiệu năng & Tốc độ** | ⚡ Siêu nhanh (Zero dependency, ~3KB) | ⚠️ Phụ thuộc CDN mạng ngoài, thêm ~40KB | 🐢 Nặng, tốn CPU/GPU |
| **Hiệu ứng 3D** | ✨ Nghiêng 3D mượt mà, phản quang, chiều sâu | ✨ Nghiêng 3D tiêu chuẩn | 🌟 Rất đẹp nhưng quá phức tạp |
| **Tính ổn định & Cloud** | 🛡️ 100% không sợ lỗi mạng hay CDN chết | ⚠️ Nguy cơ lỗi khi CDN bị gián đoạn | ⚠️ Dễ giật lag trên máy yếu |
| **Khả năng tùy biến** | 🎯 May đo chính xác cho chủ đề y tế học đường | 🔄 Bị gò bó theo template thư viện | ❌ Khó bảo trì code |

---

## 🚀 4. Kế Hoạch Triển Khai Chi Tiết (Từng Bước)

### Bước 1: Xây Dựng Hệ Thống CSS 3D & Animation Tokens trong `index.css`
- Thiết lập không gian 3D: `perspective: 1000px`, `transform-style: preserve-3d`.
- Các class Scroll Reveal:
  - `.reveal-on-scroll`: Trạng thái ban đầu mờ ảo, đẩy lùi về trục Z (`opacity: 0; transform: translateY(30px) perspective(1000px) rotateX(8deg);`).
  - `.reveal-on-scroll.is-visible`: Trạng thái hoàn chỉnh (`opacity: 1; transform: translateY(0) perspective(1000px) rotateX(0); transition: all 0.7s cubic-bezier(0.16, 1, 0.3, 1)`).
- Hiệu ứng 3D Card Tilt & Specular Shine:
  - Khi hover, các phần tử con bên trong card (`.service-icon`, `h3`, `p`) nổi lên bề mặt với `translateZ(20px)`.

### Bước 2: Viết Logic Scroll-Driven Observer & 3D Tilt trong `index.js`
- Dùng `IntersectionObserver` tự động quan sát khi các phần tử cuộn vào 15% viewport để thêm class `.is-visible`.
- Hỗ trợ Stagger Delay: Tự động cộng dồn độ trễ `transition-delay: 100ms * index` cho các card trong cùng một grid.
- Viết module 3D Tilt nhẹ nhàng: Bắt sự kiện `mousemove` và `mouseleave` trên các card `.enhanced-card` để tính toán góc nghiêng tự nhiên theo vị trí con trỏ chuột.

### Bước 3: Cập nhật Cấu Trúc `index.html`
- Gắn class `.reveal-on-scroll` và thuộc tính stagger vào các khối:
  - Hero content & trust badges.
  - Section Services (4 Service cards).
  - Section About (3 Value cards).
  - Section Quick Stats (3 Stat cards).
  - Section Contact & Form.

### Bước 4: Kiểm Chứng & Tối Ưu Hóa (Verification)
- Kiểm tra tính tương thích trên mọi kích thước màn hình (Desktop, Tablet, Mobile).
- Đảm bảo trên thiết bị di động (cảm ứng) hiệu ứng tự động fallback mượt mà không bị giật.
- Chạy kiểm thử tự động `mvnw test-compile` đảm bảo không ảnh hưởng backend.
