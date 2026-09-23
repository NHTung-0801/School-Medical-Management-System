package com.medical.schoolMedical.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$";
    private static final String PHONE_REGEX = "^0[35789]\\d{8}$";

    // Họ tên hợp lệ: chỉ ký tự Unicode (chữ cái mọi ngôn ngữ), dấu cách và dấu gạch nối; độ dài 2-100 ký tự
    private static final Pattern FULLNAME_VALID_CHARS = Pattern.compile("^[\\p{L} \\-'.]+$");
    // Ký tự cần loại bỏ khi sanitize: giữ ký tự Unicode, dấu cách, gạch nối, dấu chấm, dấu nháy đơn
    private static final Pattern FULLNAME_INVALID_CHARS = Pattern.compile("[^\\p{L} \\-'.]");

    public static boolean isValidEmail(String email) {
        return email != null && !email.contains("..") && email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.trim().matches(PHONE_REGEX);
    }

    /**
     * Kiểm tra họ tên có hợp lệ không:
     * - Không null/rỗng
     * - Độ dài từ 2 đến 100 ký tự
     * - Chỉ gồm ký tự Unicode (chữ cái tiếng Việt, quốc tế), khoảng trắng, dấu gạch nối, dấu chấm, dấu nháy đơn
     * - Không chứa ký tự đặc biệt như ?, #, @, số, v.v.
     */
    public static boolean isValidFullName(String name) {
        if (name == null || name.isBlank()) return false;
        String trimmed = name.trim();
        if (trimmed.length() < 2 || trimmed.length() > 100) return false;
        return FULLNAME_VALID_CHARS.matcher(trimmed).matches();
    }

    /**
     * Tự động làm sạch họ tên:
     * 1. Loại bỏ mọi ký tự không hợp lệ (?, #, @, số, ký tự đặc biệt)
     * 2. Chuẩn hóa khoảng trắng: chỉ giữ 1 khoảng trắng đơn giữa các từ
     * 3. Trim hai đầu
     */
    public static String sanitizeFullName(String name) {
        if (name == null) return "";
        // Loại bỏ ký tự không hợp lệ
        String sanitized = FULLNAME_INVALID_CHARS.matcher(name).replaceAll("");
        // Chuẩn hóa khoảng trắng
        sanitized = sanitized.trim().replaceAll("\\s+", " ");
        return sanitized;
    }

    /**
     * Chuẩn hóa họ tên toàn diện (Đồng nhất dữ liệu):
     * 1. Sanitize ký tự đặc biệt và khoảng trắng
     * 2. Viết hoa chữ cái đầu mỗi từ (Title Case) đúng chuẩn tiếng Việt và quốc tế
     * Ví dụ: "  nguyễn   văn   an  " -> "Nguyễn Văn An"
     */
    public static String normalizeFullName(String name) {
        String sanitized = sanitizeFullName(name);
        if (sanitized.isEmpty()) return "";
        String[] words = sanitized.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toTitleCase(word.charAt(0)));
            if (word.length() > 1) {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
}

