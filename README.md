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

### Kiến trúc phân tầng (Layered Architecture)
```
Browser / Client (Thymeleaf, Bootstrap, JS)
       │
       ▼
Controller Layer (Spring MVC Controllers, Spring Security Filter)
       │
       ▼
Service Layer (Business Logic, Transaction Management, Notification)
       │
       ▼
Mapper Layer (MapStruct DTO <-> Entity)
       │
       ▼
Repository Layer (Spring Data JPA / Hibernate)
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
| **Giao diện** | Thymeleaf, Thymeleaf Extras Spring Security 6, Bootstrap | - |
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
│   │   │   ├── controller/          # Tầng Controller tiếp nhận HTTP Request
│   │   │   │   ├── admin/           # Điều hướng & Quản trị Dashboard
│   │   │   │   ├── auth/            # Xác thực, OTP, Đăng nhập
│   │   │   │   ├── manager/         # Quản lý học sinh, lớp học
│   │   │   │   ├── parent/          # Tiếp nhận thông báo, gửi thuốc, xác nhận khám
│   │   │   │   ├── schoolNurse/     # Nghiệp vụ y tá, tiêm chủng, khám bệnh
│   │   │   │   └── user/            # Trang thông tin cá nhân, đổi mật khẩu
│   │   │   ├── dto/                 # Data Transfer Objects (Payloads)
│   │   │   ├── entities/            # JPA Entities (Ánh xạ bảng CSDL)
│   │   │   ├── enums/               # Enums (Role, Gender, ConsentStatus)
│   │   │   ├── exceptions/          # Xử lý lỗi toàn cục (Global Exception Handler)
│   │   │   ├── mapper/              # MapStruct interfaces chuyển đổi DTO-Entity
│   │   │   ├── repositories/        # Spring Data JPA Repositories
│   │   │   ├── security/            # Cấu hình Spring Security & UserDetails
│   │   │   ├── service/             # Xử lý nghiệp vụ (Business Logic)
│   │   │   └── util/                # Tiện ích bổ trợ (Validation, Helpers)
│   │   └── resources/
│   │       ├── static/              # CSS, JS, hình ảnh, tài nguyên tĩnh
│   │       ├── templates/           # Thymeleaf HTML Templates
│   │       │   ├── admin/           # Giao diện Admin
│   │       │   ├── nurse/           # Giao diện Y tá
│   │       │   ├── parent/          # Giao diện Phụ huynh
│   │       │   ├── manager/         # Giao diện Quản lý
│   │       │   └── common/          # Giao diện dùng chung & Email templates
│   │       └── application.properties # Cấu hình ứng dụng, Database, Mail
├── pom.xml                          # Khai báo thư viện & cấu hình build Maven
└── README.md                        # Tài liệu hướng dẫn dự án
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

## 🛠️ Lộ Trình Nâng Cấp Tiếp Theo (Roadmap)

- [ ] **Vá bảo mật:** Triển khai cơ chế Reset Password an toàn với Signed Token/OTP, kích hoạt lại CSRF.
- [ ] **Sửa lỗi Hibernate:** Tối ưu hóa truy vấn chi tiết sự kiện y tế tránh `MultipleBagFetchException`.
- [ ] **Tái cấu trúc thư mục (Refactoring):** Chuẩn hóa toàn bộ package Controller và sửa lỗi chính tả ở Repositories.
- [ ] **Kiểm thử tự động:** Xây dựng bộ Unit Test & Integration Test đạt độ phủ trên 70% với JUnit 5 & Mockito.
- [ ] **Xuất báo cáo:** Tích hợp xuất file Excel / PDF cho hồ sơ sức khỏe và sổ theo dõi tiêm chủng định kỳ.

---

## 👤 Tác Giả & Bản Quyền (Author & License)

* **Phát triển bởi:** **NHTung-0801** ([GitHub Profile](https://github.com/NHTung-0801))
* **Email liên hệ:** `kiritohackiem05@gmail.com`
* Dự án được phân phối dưới giấy phép [MIT License](LICENSE).