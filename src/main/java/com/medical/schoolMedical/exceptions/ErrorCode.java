package com.medical.schoolMedical.exceptions;

public enum ErrorCode {

    // Đăng nhập
    USERNAME_NOT_EXISTS("ERR001", "Tên đăng nhập không tồn tại."),
    INVALID_PASSWORD("ERR002", "Mật khẩu không chính xác."),
    USERNAME_EXISTS("ERR003", "Tên đăng nhập đã tồn tại."),
    CONTAINS_WHITESPACE("ERR004", "Tên đăng nhập không được chứa khoảng trắng."),
    USER_NOT_EXISTS("ERR005", "Người dùng không tồn tại."),
    USERNAME_NOT_FOUND("ERR006", "Người dùng không tồn tại"),

//    INTERNAL_ERROR,
//health check consent
    HEALTH_CHECK_CONSENT_NOT_FOUND("ERR050", "Phiếu đồng ý kiểm tra sức khỏe không tồn tại."),
    SURVEY_EXPIRED("ERR051", "Phiếu đã hết hạn."),
    SAVE_HEALTH_CHECK_CONSENT_FAILED("ERR052", "Lưu phiếu đồng ý kiểm tra sức khỏe thất bại."),
    CHECK_DATE_INVALID("ERR048", "Ngày kiểm tra không được là ngày trong quá khứ"),

//    Mã lỗi của health check record
    SCHOOL_NURSE_NOT_EXISTS("ERR053", "School nurse không tồn tại."),
    HEALTH_CHECK_RECORD_NOT_EXISTS("ERR054", "Bản ghi kết quả khám sức khỏe không tồn tại."),
    SAVE_HEALTH_CHECK_RECORD_FAILED("ERR055", "Lưu kết quả khám sức khỏe thất bại."),

//Lỗi tìm kiếm role:
    PARENT_NOT_EXISTS("ERR056", "Parent không tồn tại."),

    // Lỗi phía student
    STUDENT_NOT_FOUND("ERR057", "Học sinh không tồn tại."),

    //    Lỗi health check schedule
    SAVE_HEALTH_CHECK_SCHEDULE_FAILED("ERR058", "Lưu lịch kiểm tra sức khỏe thất bại."),
    HEALTH_CHECK_SCHEDULE_NOT_EXISTS("ERR059", "lịch kiểm tra sức khỏe không tồn tại."),
    // Lỗi hệ thống
    INTERNAL_ERROR("ERR999", "Lỗi hệ thống. Vui lòng thử lại sau."),

//    Lỗi của vaccination
    SAVE_VACCINATION_SCHEDULE_FAILED("ERR060", "Lưu lịch tiêm vaccine thất bại."),
    VACCINATION_CONSENT_NOT_EXISTS("ERR061", "Phiếu xác nhận tiêm chủng không tồn tại."),
    SAVE_VACCINATION_CONSENT_FAILED("ERR062", "Lưu phiếu xác nhận tiêm chủng thất bại."),
    SAVE_VACCINATION_RECORD_FAILED("ERR063", "Lưu kết quả sau tiêm chủng thất bại."),
    VACCINATION_RECORD_NOT_EXISTS("ERR064", "Bản ghi kết quả tiêm chủng không tồn tại."),
    VACCINATION_SCHEDULE_NOT_EXISTS("ERR068", "lịch tiêm chủng không tồn tại."),

//    lỗi của student
    STUDENT_NOT_EXISTS("ERR065", "Học sinh không tồn tại."),

// lỗi CONSULTATION_APPOINTMENT
    SAVE_CONSULTATION_APPOINTMENT_FAILED("ERR066", "Lưu lịch hẹn tư vấn thất bại."),
    CONSULTATION_APPOINTMENT_NOT_EXISTS("ERR067", "lịch hẹn tư vấn không tồn tại."),



    // Lỗi ở medicine

    MEDICAL_SUPPLY_NOT_FOUND("ERR070", "Vật tư y tế không tồn tại."),
    MEDICINE_NOT_FOUND("ERR071", "Thuốc không tồn tại."),

    MEDICAL_EVENT_NOT_FOUND("ERR072", "Sự kiện y tế không tồn tại."),


    // Hồ sơ sức khỏe
    HEALTH_RECORD_NOT_FOUND("ERR073", "Không tìm thấy hồ sơ sức khỏe."),

    SENT_MEDICINE_NOT_FOUND("ERR075", "Không tìm thấy bản ghi gửi thuốc.");








    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }

    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "[" + code + "] " + message;
    }

}
