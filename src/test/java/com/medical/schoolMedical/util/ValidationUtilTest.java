package com.medical.schoolMedical.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com",
            "user.name+tag@domain.co",
            "student_123@school.edu.vn",
            "first.last@sub.domain.org"
    })
    @DisplayName("Should return true for valid email formats")
    void testValidEmail(String email) {
        assertTrue(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "plainaddress",
            "@missingusername.com",
            "username@.com",
            "username@domain..com",
            "username@domain"
    })
    @DisplayName("Should return false for invalid email formats")
    void testInvalidEmail(String email) {
        assertFalse(ValidationUtil.isValidEmail(email));
    }

    @Test
    @DisplayName("Should return false when email is null")
    void testNullEmail() {
        assertFalse(ValidationUtil.isValidEmail(null));
    }
}
