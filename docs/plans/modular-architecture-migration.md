# 📋 Kế Hoạch Tái Cấu Trúc Dự Án Theo Kiến Trúc Mô-Đun (Package-by-Feature / Modular Monolith)

Tài liệu này xác định chi tiết lộ trình chuyển đổi cấu trúc dự án **School Medical Management System** từ **Package-by-Layer** (phân theo tầng kỹ thuật cồng kềnh) sang **Package-by-Feature / Modular Monolith** (phân theo từng miền nghiệp vụ gọn gàng, độc lập).

---

## 🎯 Mục Tiêu Tái Cấu Trúc

1. **Tăng tính liên kết nghiệp vụ (High Cohesion):** Mỗi nghiệp vụ (Kho dược, Tiêm chủng, Khám sức khỏe, Sự cố y tế,...) được đóng gói thành một module riêng biệt. Khi cần sửa đổi hay nâng cấp, lập trình viên chỉ cần thao tác trong đúng 1 thư mục duy nhất.
2. **Loại bỏ các thư mục quá tải (No More Bloated Folders):** Không còn tình trạng một thư mục chứa 15–25 file nằm la liệt. Mỗi module chỉ gồm 4–8 file liên quan trực tiếp đến nhau.
3. **An toàn tuyệt đối (Zero-Downtime / Incremental Slicing):** Chia quá trình di chuyển thành **6 lát cắt (slices)** độc lập. Sau mỗi lát cắt, chạy lệnh kiểm tra biên dịch (`.\mvnw clean test-compile`) đạt `BUILD SUCCESS` rồi mới chuyển sang module tiếp theo.

---

## 🗺️ Cấu Trúc Thư Mục Mục Tiêu (Target Architecture)

```text
com.medical.schoolMedical/
├── modules/
│   ├── pharmacy/                 <-- Module 1: Kho Dược & Vật Tư Y Tế
│   │   ├── controllers/          (MedicineController, MedicalSupplyController, MedicineUsageController)
│   │   ├── services/             (MedicineService, MedicalSupplyService, MedicineUsedService)
│   │   ├── repositories/         (MedicineRepository, MedicalSupplyRepository, MedicineUsedRepository, SupplyUsedRepository)
│   │   ├── entities/             (Medicine, MedicalSupply, MedicineUsed, SupplyUsed)
│   │   └── dto/                  (MedicineUsedDTO, MedicineUsedRequestDTO, SupplyUsedDTO)
│   │
│   ├── medical_event/            <-- Module 2: Sự Cố Y Tế & Cấp Cứu Học Đường
│   │   ├── controllers/          (MedicalEventController)
│   │   ├── services/             (MedicalEventService)
│   │   ├── repositories/         (MedicalEventRepository)
│   │   ├── entities/             (MedicalEvent)
│   │   └── dto/                  (MedicalEventDTO)
│   │
│   ├── healthcheck/              <-- Module 3: Khám Sức Khỏe Định Kỳ
│   │   ├── controllers/          (HealthCheckScheduleController, HealthCheckConsentController, HealthCheckRecordController, ConfirmHealthCheck, HealthCheckRecordParentController)
│   │   ├── services/             (HealthCheckScheduleService, HealthCheckConsentService, HealthCheckRecordService)
│   │   ├── repositories/         (HealthCheckScheduleRepository, HealthCheckConsentRepository, HealthCheckRecordRepository)
│   │   ├── entities/             (HealthCheckSchedule, HealthCheckConsent, HealthCheckRecord)
│   │   ├── dto/                  (HealthCheckScheduleDTO, HealthCheckConsentDTO, HealthCheckRecordDTO)
│   │   └── mappers/              (HealthCheckScheduleMapper, HealthCheckConsentMapper, HealthCheckRecordMapper)
│   │
│   ├── vaccination/              <-- Module 4: Tiêm Chủng Học Đường
│   │   ├── controllers/          (VaccinationScheduleController, VaccinationConsentController, VaccinationRecordController, ConfirmVaccination, VaccinationRecordParentController)
│   │   ├── services/             (VaccinationScheduleService, VaccinationConsentService, VaccinationRecordService)
│   │   ├── repositories/         (VaccinationScheduleRepository, VaccinationConsentRepository, VaccinationRecordRepository)
│   │   ├── entities/             (VaccinationSchedule, VaccinationConsent, VaccinationRecord)
│   │   ├── dto/                  (VaccinationScheduleDTO, VaccinationConsentDTO, VaccinationRecordDTO)
│   │   └── mappers/              (VaccinationScheduleMapper, VaccinationConsentMapper, VaccinationRecordMapper)
│   │
│   ├── consultation/             <-- Module 5: Lịch Hẹn Tư Vấn Sức Khỏe
│   │   ├── controllers/          (ConsultationAppointmentController, ConsultationAppointmentParentController)
│   │   ├── services/             (ConsultationAppointmentService)
│   │   ├── repositories/         (ConsultationAppointmentRepository)
│   │   ├── entities/             (ConsultationAppointment)
│   │   ├── dto/                  (ConsultationAppointmentDTO)
│   │   └── mappers/              (ConsultationAppointmentMapper)
│   │
│   ├── health_record/            <-- Module 6: Hồ Sơ Sức Khỏe Cá Nhân & Gửi Thuốc
│   │   ├── controllers/          (HealthRecordController, NurseHealthRecordController, SentMedicineController)
│   │   ├── services/             (HealthRecordService, SentMedicineService, SentMedicineUsageService)
│   │   ├── repositories/         (HealthRecordRepository, SentMedicineRepository, SentMedicineUsageRepository)
│   │   ├── entities/             (HealthRecord, SentMedicine, SentMedicineUsage)
│   │   └── dto/                  (SentMedicineDTO)
│   │
│   ├── user_management/          <-- Module 7: Người Dùng & Các Vai Trò (Admin, Manager, Nurse, Parent, Student)
│   │   ├── controllers/          (AdminController, ManagerController, ManagerStudentController, ParentController, SchoolNurseController, ProfileController, Home, NotificationParentController, SignupController)
│   │   ├── services/             (UserService, StudentService, ParentService, StatisticsService)
│   │   ├── repositories/         (UserRepository, StudentRepository, ParentRepository, SchoolNurseRepository, AdminRepository, ManagerRepository)
│   │   ├── entities/             (User, Student, Parent, SchoolNurse, Admin, Manager)
│   │   ├── dto/                  (UserDTO, StudentDTO, ParentDTO, SchoolNurseDTO)
│   │   └── mappers/              (UserMapper, StudentMapper, ParentMapper)
│   │
│   └── auth/                     <-- Module 8: Xác Thực, OTP & Mật Khẩu
│       ├── controllers/          (OtpController, PasswordController)
│       ├── services/             (OtpService, EmailService)
│       └── security/             (CustomUserDetails, CustomUserDetailsService, SecurityConfig)
│
└── shared/                       <-- Hạ tầng & Tiện ích dùng chung
    ├── enums/                    (Role, Gender, ConsentStatus)
    └── exceptions/               (BusinessException, ErrorCode, GlobalExceptionHandler)
```

---

## 🔄 Lộ Trình Triển Khai Từng Bước (Incremental Migration Slices)

Để đảm bảo an toàn tuyệt đối, chúng ta sẽ thực hiện di chuyển và kiểm thử theo từng lát cắt nghiệp vụ:

1. **Lát cắt 1: Module Pharmacy (Kho Dược & Vật Tư Y Tế)**
   - Di chuyển các file liên quan (`Medicine`, `MedicalSupply`, `MedicineUsed`, `SupplyUsed`, DTO, Repo, Service, Controller).
   - Chạy `.\mvnw clean test-compile` -> Phải đạt `BUILD SUCCESS`.
   - Tạo commit local riêng biệt cho module Pharmacy.

2. **Lát cắt 2: Module Medical Event (Sự Cố Y Tế)**
   - Di chuyển `MedicalEvent` entity, DTO, Repo, Service, Controller.
   - Chạy `.\mvnw clean test-compile` -> Phải đạt `BUILD SUCCESS`.
   - Tạo commit local riêng biệt.

3. **Lát cắt 3: Module Health Check (Khám Sức Khỏe Định Kỳ)**
   - Di chuyển Schedule, Consent, Record của Health Check.
   - Chạy `.\mvnw clean test-compile` -> Phải đạt `BUILD SUCCESS`.
   - Tạo commit local riêng biệt.

4. **Lát cắt 4: Module Vaccination (Tiêm Chủng Học Đường)**
   - Di chuyển Schedule, Consent, Record của Tiêm chủng.
   - Chạy `.\mvnw clean test-compile` -> Phải đạt `BUILD SUCCESS`.
   - Tạo commit local riêng biệt.

5. **Lát cắt 5: Module Consultation & Health Record (Tư Vấn, Hồ Sơ Sức Khỏe & Gửi Thuốc)**
   - Di chuyển `ConsultationAppointment`, `HealthRecord`, `SentMedicine`.
   - Chạy `.\mvnw clean test-compile` -> Phải đạt `BUILD SUCCESS`.
   - Tạo commit local riêng biệt.

6. **Lát cắt 6: Module User Management, Auth & Shared**
   - Di chuyển User, Student, Parent, Nurse, Admin, Manager, Auth, Security, Enums, Exceptions.
   - Dọn dẹp các thư mục rỗng cũ (`entities/`, `repositories/`, `service/`, `dto/`, `mapper/`, `controller/`).
   - Chạy `.\mvnw clean test-compile` toàn diện -> Phải đạt `BUILD SUCCESS`.
   - Tạo commit local hoàn thành tái cấu trúc.
