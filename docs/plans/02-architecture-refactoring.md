# 🏗️ Kế Hoạch Chi Tiết Giai Đoạn 2: Tái Cấu Trúc Mã Nguồn & Kiến Trúc (Architecture & Cleanup)

- **Mã kế hoạch:** `PLAN-02`
- **Tập tin liên quan:**
  - `src/main/java/com/medical/schoolMedical/repositories/ParentRepositoty.java` (Đổi tên)
  - `src/main/java/com/medical/schoolMedical/service/UserService.java`
  - `src/main/java/com/medical/schoolMedical/service/StudentService.java`
  - `src/main/java/com/medical/schoolMedical/service/StatisticsService.java`
  - `src/main/java/com/medical/schoolMedical/service/ParentService.java`
  - `src/main/java/com/medical/schoolMedical/controller/user/ProfileController.java`
  - `src/main/java/com/medical/schoolMedical/controller/user/*` (Di chuyển sang `schoolNurse` và `parent`)
  - `src/main/java/com/medical/schoolMedical/service/HealthCheckConsentService.java` (Khử `@Lazy`)
  - `src/main/java/com/medical/schoolMedical/controller/admin/Trangchutamthoi.java` (Xóa)
  - `src/main/java/com/medical/schoolMedical/entities/*.java` (Thay `@Data` bằng `@Getter`/`@Setter`)
  - `src/main/java/com/medical/schoolMedical/exceptions/ErrorCode.java` (Sửa mã lỗi trùng)
- **Trạng thái:** ⏳ Đang chờ người dùng phê duyệt (Pending Review)

---

## 1. Mục Tiêu (Objectives)

1. **Chuẩn hóa quy ước đặt tên:** Sửa lỗi chính tả `ParentRepositoty` -> `ParentRepository` trên toàn bộ dự án.
2. **Quy hoạch lại cấu trúc Controller:** Di chuyển các Controller về đúng package theo phân quyền vai trò (`schoolNurse`, `parent`, `manager`).
3. **Triệt tiêu phụ thuộc vòng (Circular Dependencies):** Loại bỏ hoàn toàn `@Lazy` giữa `HealthCheckConsentService`, `HealthCheckScheduleService` và `HealthCheckRecordService`.
4. **Dọn dẹp mã nguồn thừa (Dead Code):** Xóa bỏ file `Trangchutamthoi.java` và các đoạn code thử nghiệm.
5. **Chuẩn hóa JPA Entities:** Thay thế `@Data` bằng `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` để triệt tiêu nguy cơ `StackOverflowError` trong các quan hệ hai chiều.
6. **Chuẩn hóa danh mục mã lỗi:** Khắc phục tình trạng trùng lặp mã lỗi `ERR057` và `ERR059` trong `ErrorCode.java`.

---

## 2. Chi Tiết Các Bước Thực Hiện (Implementation Steps)

### 🔹 Bước 2.1: Sửa lỗi chính tả `ParentRepositoty` -> `ParentRepository`
- **File đổi tên:** `ParentRepositoty.java` -> `ParentRepository.java`.
- **Tên interface:** `public interface ParentRepository extends JpaRepository<Parent, Long>`.
- **Cập nhật các nơi inject:**
  - `UserService.java`: `ParentRepository parentRepository;`
  - `StudentService.java`: `ParentRepository parentRepository;`
  - `StatisticsService.java`: `ParentRepository parentRepository;`
  - `ParentService.java`: `ParentRepository parentRepository;`
  - `ProfileController.java`: `ParentRepository parentRepository;`

### 🔹 Bước 2.2: Tái phân bổ Controller về đúng Package theo Role
- Hiện tại package `com.medical.schoolMedical.controller.user` đang chứa nhiều controller không thuộc về user chung. Chúng ta sẽ di chuyển:
  - `NurseHealthRecordController.java` (`/nurse/health-record`) -> `controller.schoolNurse`
  - `MedicineController.java` (`/nurse/medicines`) -> `controller.schoolNurse`
  - `MedicalSupplyController.java` (`/nurse/medical-supplies`) -> `controller.schoolNurse`
  - `MedicalEventController.java` (`/nurse/medical-events`) -> `controller.schoolNurse`
  - `HealthRecordController.java` (`/parent/health-record`) -> `controller.parent`
  - `ManagerController.java` (`/manager`) -> `controller.manager`
- Giữ lại trong `controller.user`: `Home.java`, `ProfileController.java`, `PasswordController.java`.

### 🔹 Bước 2.3: Khử triệt để phụ thuộc vòng (`@Lazy`)
- **Phân tích:**
  - `HealthCheckConsentService` đang khai báo `@Lazy @Autowired HealthCheckRecordService healthCheckRecordService;` nhưng **hoàn toàn không sử dụng** trong class này -> **Xóa bỏ field thừa này**.
  - `HealthCheckConsentService` gọi `healthCheckScheduleService.getHealthCheckScheduleById(scheduleId)` để lấy `HealthCheckSchedule`. Nhưng `HealthCheckConsentService` đã có sẵn `HealthCheckScheduleRepository` -> Thay bằng: `healthCheckScheduleRepository.findById(scheduleId).orElseThrow(...)`.
  - Sau khi bỏ 2 điểm trên, `HealthCheckConsentService` **không còn phụ thuộc vào bất kỳ Service nào khác**, loại bỏ được hoàn toàn annotation `@Lazy`.

### 🔹 Bước 2.4: Dọn dẹp Code rác (Dead Code Hygiene)
- Xóa file [Trangchutamthoi.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/controller/admin/Trangchutamthoi.java).
- Xóa các đoạn code chú thích (comment) thừa trong `AdminController.java`, `UserService.java`.

### 🔹 Bước 2.5: Thay thế `@Data` trên các JPA Entity
- Trong Hibernate/JPA, `@Data` tự sinh `equals()`, `hashCode()` và `toString()` duyệt qua toàn bộ quan hệ hai chiều, rất dễ gây lỗi đệ quy vô tận `StackOverflowError`.
- Thay `@Data` trên các Entity ([User.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/entities/User.java), [Parent.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/entities/Parent.java), [Student.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/entities/Student.java), [HealthRecord.java](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/src/main/java/com/medical/schoolMedical/entities/HealthRecord.java),...) bằng:
  - `@Getter`
  - `@Setter`
  - `@NoArgsConstructor`
  - `@AllArgsConstructor` (nếu có Builder)

### 🔹 Bước 2.6: Chuẩn hóa mã lỗi trong `ErrorCode.java`
- `CHECK_DATE_INVALID`: Đổi mã lỗi từ `ERR057` sang `ERR048`.
- `STUDENT_NOT_FOUND`: Giữ nguyên `ERR057`.
- `HEALTH_CHECK_SCHEDULE_NOT_EXISTS`: Giữ nguyên `ERR059`.
- `VACCINATION_SCHEDULE_NOT_EXISTS`: Đổi mã lỗi từ `ERR059` sang `ERR068`.

---

## 3. Kế Hoạch Xác Minh (Verification Plan)

1. **Biên dịch dự án:**
   ```powershell
   .\mvnw clean test-compile
   ```
   Đảm bảo biên dịch thành công 100%, không bị lỗi import sai package hay thiếu dependency.
2. **Kiểm tra Spring Context:**
   Khởi động ứng dụng kiểm tra không còn cảnh báo phụ thuộc vòng (Circular Reference) trong log khởi động.
