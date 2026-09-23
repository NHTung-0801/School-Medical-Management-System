# 🚀 Kế Hoạch Chi Tiết Giai Đoạn 5: Nâng Cấp Tính Năng & Sẵn Sàng Vận Hành (Enhancements & Production Readiness)

- **Mã kế hoạch:** `PLAN-05`
- **Tập tin liên quan:**
  - `src/main/resources/templates/fragments/header.html`
  - `src/main/java/com/medical/schoolMedical/modules/notification/`
  - `src/main/java/com/medical/schoolMedical/modules/report/`
  - `.github/workflows/ci.yml`
  - `Dockerfile`
  - `docker-compose.yml`
- **Trạng thái:** ✅ Đã hoàn thành cốt lõi (Core Completed - Excel Export, In-App Notification Center, CI/CD Pipeline)

---

## 1. Mục Tiêu Tổng Thể (Objectives)

1. **Xuất báo cáo nghiệp vụ (Export Excel/PDF):** Cho phép Nhà trường, Quản trị viên và Nhân viên y tế kết xuất danh sách khám sức khỏe, nhật ký tiêm chủng và hồ sơ bệnh án thành định dạng tệp chuẩn mực.
2. **Hệ thống thông báo trong ứng dụng (In-App Notifications):** Cung cấp chuông thông báo thời gian thực cho Phụ huynh khi có phiếu lấy ý kiến mới, lịch khám mới hoặc cập nhật thuốc/sự cố y tế của con em.
3. **Tự động hóa CI/CD:** Thiết lập GitHub Actions Workflow tự động hóa việc biên dịch, kiểm thử (Unit & Integration Tests), kiểm soát độ bao phủ mã nguồn (JaCoCo) và đóng gói Docker Image khi có thay đổi code.
4. **Chuẩn hóa hạ tầng vận hành (Production Readiness):** Đóng gói Docker đa tầng nhẹ, bảo mật với quyền non-root user và cấu hình giám sát liveness/readiness probe.

---

## 2. Chi Tiết Các Hạng Mục Triển Khai

### 🔹 Hạng Mục 5.1: Xuất Báo Cáo Nghiệp Vụ (Excel & PDF Export)

#### 1. Xuất danh sách khám sức khỏe & tiêm chủng (Excel):
- **Thư viện đề xuất:** `Apache POI` (poi-ooxml).
- **Phạm vi chức năng:**
  - Y tá / Quản trị viên có thể xuất kết quả khám sức khỏe định kỳ theo đợt ra file `.xlsx` gồm các cột: STT, Mã định danh, Họ và tên học sinh, Lớp, Chiều cao, Cân nặng, Thị lực (mắt trái, mắt phải), Đánh giá thể trạng, Ghi chú của y tá.
  - Xuất danh sách theo dõi tiêm chủng: Loại vắc-xin, ngày tiêm, vị trí tiêm, phản ứng sau tiêm.
- **Thiết kế kiến trúc:**
  - Tạo `ExcelExportService` tại module tương ứng hoặc `common/utils/ExcelExportUtil.java`.
  - Endpoint: `GET /nurse/healthCheckRecord/export-excel?scheduleId={id}` và `GET /nurse/vaccinationRecord/export-excel?scheduleId={id}`.
  - Thiết lập Header HTTP: `Content-Disposition: attachment; filename=BaoCaoKhamSucKhoe_{scheduleId}.xlsx`.

#### 2. Xuất phiếu đồng ý / Thẻ y tế điện tử (PDF):
- **Thư viện đề xuất:** `OpenPDF` hoặc `iText7`.
- **Phạm vi chức năng:**
  - Cho phép Phụ huynh in hoặc tải về Thẻ y tế điện tử / Phiếu xác nhận đồng ý khám & tiêm chủng có chữ ký điện tử / xác nhận của phụ huynh.

---

### 🔹 Hạng Mục 5.2: Hệ Thống Thông Báo Trong Ứng Dụng (In-App Notification Center)

#### 1. Cơ sở dữ liệu & Entity:
- Tạo entity `Notification`:
  - `id`: Long (Khóa chính)
  - `recipient`: User (Người nhận)
  - `title`: String (Tiêu đề ngắn gọn)
  - `message`: String (Nội dung chi tiết)
  - `type`: NotificationType (CONSENT_REQUEST, HEALTH_RECORD_UPDATE, SENT_MEDICINE_STATUS, SYSTEM)
  - `targetUrl`: String (Đường dẫn điều hướng khi click vào thông báo)
  - `isRead`: boolean (Trạng thái đã đọc hay chưa)
  - `createdAt`: LocalDateTime

#### 2. Luồng nghiệp vụ:
- Khi Y tá gửi phiếu đồng ý khám/tiêm hoặc cập nhật nhật ký uống thuốc -> Kích hoạt `NotificationService.createNotification(...)`.
- Trên giao diện thanh điều hướng (`header.html`), hiển thị biểu tượng Chuông kèm số lượng thông báo chưa đọc (Badge counter).
- Click vào chuông hiển thị menu dropdown các thông báo gần nhất và nút "Đánh dấu tất cả là đã đọc".

---

### 🔹 Hạng Mục 5.3: Tự Động Hóa CI/CD & Đóng Gói Docker

#### 1. Trạng thái Docker hóa: *(Đã hoàn thành - 100%)*
- Đã có `Dockerfile` đa tầng (multi-stage build):
  - Stage 1 (Builder): Eclipse Temurin 21 Maven.
  - Stage 2 (Runtime): JRE 21 alpine/distroless, tạo user bảo mật non-root `spring:spring`, cấu hình healthcheck curl `http://localhost:8080/yte/actuator/health`.
- Đã có `docker-compose.yml`:
  - Khởi tạo đồng thời Service Spring Boot App và Service MySQL 8.0.
  - Cấu hình mạng nội bộ `medical-network` và volume `db-data`.
  - Cấu hình ràng buộc thứ tự khởi động `depends_on` với điều kiện `service_healthy` từ MySQL.

#### 2. Kế hoạch thiết lập GitHub Actions (`.github/workflows/ci.yml`):
- **Trigger:** Mỗi khi có Pull Request hoặc Push vào các nhánh `main`, `master`, `develop`.
- **Các bước thực thi (Jobs):**
  1. `Checkout Code`: Tải mã nguồn mới nhất.
  2. `Set up JDK 21`: Cấu hình môi trường Java 21 Eclipse Temurin có kích hoạt bộ nhớ đệm (cache) Maven.
  3. `Run Unit & Integration Tests`: Chạy lệnh `mvn clean test` trên database H2 in-memory.
  4. `Verify Test Coverage`: Kiểm tra báo cáo JaCoCo coverage đảm bảo chất lượng.
  5. `Build Docker Image`: Xác minh Dockerfile biên dịch thành công mà không có cảnh báo nghiêm trọng.

---

## 3. Lộ Trình Triển Khai Chi Tiết

| Bước | Nhiệm Vụ | Đầu Ra Kỳ Vọng | Trạng Thái |
| :--- | :--- | :--- | :--- |
| **5.3b** | Thiết lập GitHub Actions CI workflow | File `.github/workflows/ci.yml` kiểm tra build tự động | ✅ Hoàn thành (86/86 Tests Pass) |
| **5.1** | Tích hợp xuất Excel cho lịch khám & tiêm chủng | Apache POI, ExcelExportService, nút "Xuất Excel" trên UI | ✅ Hoàn thành |
| **5.2** | Chuông thông báo In-App Notifications | Entity, Repository, Service, Notification Dropdown & Nav Dots | ✅ Hoàn thành |
| **5.4** | Bổ sung Actuator & hoàn thiện tài liệu vận hành | Endpoint `/actuator/health`, tài liệu hướng dẫn triển khai Production | 🔄 Sẵn sàng bàn giao |

