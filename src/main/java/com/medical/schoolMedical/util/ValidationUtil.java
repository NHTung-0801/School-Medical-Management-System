package com.medical.schoolMedical.util;

public class ValidationUtil {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$";
    private static final String PHONE_REGEX = "^0[35789]\\d{8}$";

    public static boolean isValidEmail(String email) {
        return email != null && !email.contains("..") && email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.trim().matches(PHONE_REGEX);
    }
}

