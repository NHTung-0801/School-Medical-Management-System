# 🏥 School Medical Management System (Hệ Thống Quản Lý Y Tế Học Đường)

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge&logo=openjdk" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.0-brightgreen.svg?style=for-the-badge&logo=springboot" alt="Spring Boot 3.5.0" />
  <img src="https://img.shields.io/badge/Spring_Security-6-blue.svg?style=for-the-badge&logo=springsecurity" alt="Spring Security 6" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue.svg?style=for-the-badge&logo=mysql" alt="MySQL 8.0" />
  <img src="https://img.shields.io/badge/Thymeleaf-HTML5-green.svg?style=for-the-badge&logo=thymeleaf" alt="Thymeleaf" />
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge" alt="License MIT" />
</p>

---

## 📖 Giới Thiệu Dự Án (Project Overview)

**School Medical Management System** là giải pháp phần mềm chuyển đổi số toàn diện công tác quản lý y tế tại các cơ sở giáo dục. Hệ thống kết nối liền mạch giữa **Nhà trường - Nhân viên y tế - Phụ huynh học sinh - Ban giám hiệu**, giúp số hóa hoàn toàn quy trình theo dõi sức khỏe học đường, quản lý hồ sơ bệnh án, tiêm chủng, sự cố cấp cứu y tế, và tiếp nhận thuốc gửi hàng ngày từ phụ huynh.

Dự án được xây dựng trên nền tảng **Java 21** và **Spring Boot 3.5.0**, áp dụng kiến trúc phân tầng (Layered Architecture), bảo mật phân quyền Role-Based Access Control (RBAC) với **Spring Security 6**.

---

## ✨ Tính Năng Nổi Bật (Key Features)

Hệ thống cung cấp 4 phân hệ tính năng chuyên biệt tương ứng với 4 vai trò:

### 1. 🧑‍⚕️ Phân Hệ Y Tá Học Đường (School Nurse)
* **Khám sức khỏe định kỳ (Health Check):**
  * Thiết lập lịch khám theo khối/lớp, gửi phiếu xin ý kiến (`HealthCheckConsent`) tự động đến phụ huynh.
  * Nhập liệu và quản lý kết quả khám chi tiết (`HealthCheckRecord`): thể lực (chiều cao, cân nặng, BMI), thị lực, răng hàm mặt, tai mũi họng, tim phổi.
* **Quản lý tiêm chủng (Vaccination):**
  * Lập kế hoạch tiêm chủng (`VaccinationSchedule`), theo dõi tỷ lệ phụ huynh đồng ý/từ chối.
  * Ghi nhận kết quả sau tiêm chủng (`VaccinationRecord`), theo dõi các phản ứng sau tiêm.
* **Xử lý sự cố y tế & Sơ cấp cứu (`MedicalEvent`):**
  * Ghi nhận tức thì các sự cố tai nạn, ốm đau phát sinh tại trường.
  * Theo dõi quy trình sơ cứu ban đầu, phương án điều trị và tự động trừ vật tư y tế/thuốc đã dùng.
* **Tủ thuốc & Vật tư tiêu hao:**
  * Quản lý danh mục thuốc (`Medicine`) và trang thiết bị vật tư y tế (`MedicalSupply`).
  * Tiếp nhận thuốc gửi từ phụ huynh (`SentMedicine`) và ghi nhật ký uống thuốc theo khung giờ (`SentMedicineUsage`).
* **Lịch hẹn tư vấn y tế (`ConsultationAppointment`):**
  * Tiếp nhận và điều phối các lịch hẹn tư vấn sức khỏe giữa phụ huynh và y tá.

### 2. 👨‍👩‍👦 Phân Hệ Phụ Huynh (Parent)
* **Theo dõi hồ sơ sức khỏe con em:** Xem chi tiết tiểu sử bệnh nền, dị ứng, chiều cao, cân nặng, thị lực qua từng năm học.
* **Phê duyệt trực tuyến:** Xác nhận hoặc từ chối phiếu lấy ý kiến khám sức khỏe và tiêm chủng ngay trên hệ thống.
* **Dịch vụ gửi thuốc trực tuyến:** Khai báo danh mục thuốc cần cho con uống tại trường kèm liều lượng và hướng dẫn chi tiết.
* **Đặt lịch tư vấn:** Đăng ký lịch gặp trực tiếp hoặc trao đổi y tế với y tá nhà trường khi con có vấn đề sức khỏe.

### 3. 👨‍💼 Phân Hệ Quản Trị Viên (Admin)
* **Dashboard trực quan hóa dữ liệu:**
  * Biểu đồ trực quan theo tháng/năm về số ca khám sức khỏe, tiêm chủng, và sự cố y tế học đường.
  * Thống kê số lượng học sinh, phụ huynh, nhân sự y tế trong hệ thống.
* **Quản lý người dùng:** Thêm mới, chỉnh sửa, phân quyền (RBAC), và khóa/xóa mềm (`Soft Delete`) tài khoản.

### 4. 🏢 Phân Hệ Quản Lý / Ban Giám Hiệu (Manager)
* Quản lý danh sách học sinh theo từng lớp học và liên kết phụ huynh.
* Giám sát tình hình chăm sóc y tế toàn trường nhằm có các biện pháp nâng cao thể chất học đường kịp thời.

---

## 🏗️ Kiến Trúc & Công Nghệ (Architecture & Tech Stack)

### Kiến Trúc Modular Monolith (Package-by-Feature)
Hệ thống được thiết kế theo mô hình **Modular Monolith (Package-by-Feature)** kết hợp với phân lớp nội bộ (Controller - Service - Repository - Entity - DTO - Mapper) trong từng module độc lập:

```
                      Browser / Client (Thymeleaf, Bootstrap 5, JS)
                                            │
                                            ▼
                    Spring Security 6 (RBAC, Authentication Filter)
                                            │
                                            ▼
┌─────────────────────────────────────── Modules ──────────────────────────────────────┐
│                                                                                      │
│  ┌────────────────┐  ┌─────────────────┐  ┌──────────────┐  ┌─────────────────────┐  │
│  │      Auth      │  │ User Management │  │   Pharmacy   │  │    Medical Event    │  │
│  └────────────────┘  └─────────────────┘  └──────────────┘  └─────────────────────┘  │
│  ┌────────────────┐  ┌─────────────────┐  ┌──────────────┐  ┌─────────────────────┐  │
│  │  Health Check  │  │   Vaccination   │  │ Consultation │  │    Health Record    │  │
│  └────────────────┘  └─────────────────┘  └──────────────┘  └─────────────────────┘  │
│                                                                                      │
└──────────────────────────────────────────┬───────────────────────────────────────────┘
                                           │
                         ┌─────────────────┴─────────────────┐
                         │ Common (Schedulers, Controllers)  │
                         │ Core (Enums, Exceptions, Util)    │
                         └─────────────────┬─────────────────┘
                                           │
                                           ▼
                                 Database (MySQL 8.0)
```

### Công nghệ sử dụng
| Thành phần | Công nghệ / Thư viện | Phiên bản |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java (OpenJDK) | 21 (LTS) |
| **Framework lõi** | Spring Boot | 3.5.0 |
| **Bảo mật** | Spring Security, BCrypt | 6.x |
| **ORM & Data** | Spring Data JPA, Hibernate, MySQL Driver | 3.5.0 |
| **Giao diện** | Thymeleaf, Thymeleaf Extras Spring Security 6, Bootstrap | 5.x |
| **Mapping & Boilerplate** | MapStruct, Project Lombok | 1.5.5.Final / 1.18.30 |
| **Email Service** | Jakarta Mail, JavaMailSender | - |
| **Cơ sở dữ liệu** | MySQL Server | 8.0+ |

---

## 📂 Cấu Trúc Mã Nguồn (Project Structure)

```text
School-Medical-Management-System/
├── src/
│   ├── main/
│   │   ├── java/com/medical/schoolMedical/
│   │   │   ├── common/                  # Thành phần dùng chung toàn hệ thống
│   │   │   │   ├── controllers/         # HomeController, ErrorController, ErrorPageController
│   │   │   │   └── schedulers/          # ConsentStatusSchedulerService (Cronjob quét phiếu hết hạn)
│   │   │   ├── enums/                   # Enums hệ thống (Role, Gender, ConsentStatus, BloodType...)
│   │   │   ├── exceptions/              # Bắt lỗi toàn cục (GlobalExceptionHandler, BusinessException, ErrorCode)
│   │   │   ├── security/                # Spring Security, CustomUserDetails, CustomSuccessHandler
│   │   │   ├── util/                    # Tiện ích bổ trợ (ValidationUtil, Helpers)
│   │   │   └── modules/                 # Các lát cắt nghiệp vụ độc lập (Package-by-Feature)
│   │   │       ├── auth/                # Đăng nhập, đăng ký, cấp lại mật khẩu OTP, UserDetails
│   │   │       ├── user_management/     # Quản trị tài khoản, phân quyền, học sinh, phụ huynh, y tá
│   │   │       ├── pharmacy/            # Kho thuốc, vật tư y tế, quản lý gửi thuốc & nhật ký uống thuốc
│   │   │       ├── medical_event/       # Xử lý sự cố sơ cứu, cấp cứu y tế học đường
│   │   │       ├── healthcheck/         # Kế hoạch khám sức khỏe định kỳ, duyệt phiếu & nhập kết quả
│   │   │       ├── vaccination/         # Kế hoạch tiêm chủng, gửi phiếu xin ý kiến & sổ tiêm
│   │   │       ├── consultation/        # Đăng ký & điều phối lịch hẹn tư vấn sức khỏe
│   │   │       └── health_record/       # Hồ sơ sức khỏe học sinh, bệnh nền, dị ứng
│   │   └── resources/
│   │       ├── static/                  # CSS, JS, hình ảnh, tài nguyên tĩnh
│   │       ├── templates/               # Giao diện Thymeleaf HTML Templates theo từng vai trò
│   │       └── application.properties   # Cấu hình Hibernate Batch, Database, Mail SMTP
│   └── test/                            # Bộ kiểm thử Unit Test & Integration Test (JUnit 5, Mockito)
├── pom.xml                              # Khai báo thư viện & cấu hình build Maven
└── README.md                            # Tài liệu hướng dẫn dự án
```

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Dự Án (Getting Started)

### 1. Yêu cầu môi trường (Prerequisites)
* **JDK:** Java Development Kit phiên bản 21 trở lên.
* **Maven:** Phiên bản 3.8+ (hoặc sử dụng wrapper `mvnw` đi kèm).
* **MySQL:** MySQL Server phiên bản 8.0 trở lên.
* **IDE:** IntelliJ IDEA, Eclipse, hoặc Visual Studio Code.

### 2. Cài đặt Cơ sở dữ liệu (Database Setup)
Khởi chạy MySQL và tạo database mới:
```sql
CREATE DATABASE db_medical CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Cấu hình ứng dụng (`application.properties`)
Mở file `src/main/resources/application.properties` và điều chỉnh thông số phù hợp:

```properties
# Server
server.port=8080
server.servlet.context-path=/yte

# Database Connection
spring.datasource.url=jdbc:mysql://localhost:3306/db_medical?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Hibernate DDL Auto (update để tự động sinh bảng)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Cấu hình gửi Mail OTP qua SMTP Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL_ADDRESS
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 4. Build và Chạy ứng dụng
Mở terminal tại thư mục dự án và thực hiện:

* **Trên Windows (PowerShell):**
  ```powershell
  .\mvnw clean spring-boot:run
  ```
* **Trên Linux/macOS:**
  ```bash
  ./mvnw clean spring-boot:run
  ```

Sau khi ứng dụng khởi động thành công, truy cập trình duyệt tại địa chỉ:
```
http://localhost:8080/yte/
```

---

## 🔒 Phân Quyền & Tài Khoản Mẫu (Roles & Accounts)

| Vai trò (Role) | URL đăng nhập | Quyền hạn chính |
| :--- | :--- | :--- |
| **`ROLE_ADMIN`** | `/yte/admin/login` | Toàn quyền quản trị hệ thống, quản lý tài khoản, xem thống kê |
| **`ROLE_NURSE`** | `/yte/login` | Quản lý lịch khám, tiêm chủng, cấp cứu y tế, tủ thuốc |
| **`ROLE_PARENT`** | `/yte/login` | Quản lý sức khỏe con, gửi thuốc, duyệt phiếu khám/tiêm |
| **`ROLE_MANAGER`** | `/yte/login` | Giám sát danh sách học sinh, tình hình y tế trường học |

---

## 🛠️ Lộ Trình Nâng Cấp Hệ Thống (Roadmap)

- [x] **Giai đoạn 1 - Vá lỗi bảo mật & Sửa lỗi nghiêm trọng:** Cấu hình xác thực an toàn, vá lỗi `MultipleBagFetchException` trên Hibernate.
- [x] **Giai đoạn 2 - Chuẩn hóa Email Service:** Tách cấu hình nhạy cảm ra Environment Variables (`.env.example`), xây dựng template HTML email hiện đại.
- [x] **Giai đoạn 3 - Tối ưu hóa hiệu năng & Ràng buộc dữ liệu:** Kích hoạt Hibernate Batch Processing (batch size = 50), bảo toàn ACID với `@Transactional`, dynamic year cho thống kê, chuẩn hóa Bean Validation trên tất cả entity & DTO.
- [x] **Tái cấu trúc kiến trúc (Refactoring):** Chuyển đổi toàn diện từ Package-by-Layer sang **Modular Monolith (Package-by-Feature)** gồm 8 module độc lập.
- [ ] **Giai đoạn 4 - Kiểm thử tự động (Testing):** Xây dựng bộ Unit Test & Integration Test toàn diện với JUnit 5, Mockito & MockMvc cho các module nghiệp vụ lõi (đạt độ phủ > 70%).
- [ ] **Giai đoạn 5 - Xuất báo cáo & Hoàn thiện:** Tích hợp xuất báo cáo Excel / PDF cho hồ sơ sức khỏe và sổ theo dõi tiêm chủng định kỳ.

---

## 👤 Tác Giả & Bản Quyền (Author & License)

* **Phát triển bởi:** **NHTung-0801** ([GitHub Profile](https://github.com/NHTung-0801))
* **Email liên hệ:** `kiritohackiem05@gmail.com`
* Dự án được phân phối dưới giấy phép [MIT License](LICENSE).