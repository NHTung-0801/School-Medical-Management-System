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

    @ParameterizedTest
    @ValueSource(strings = {
            "0912345678",
            "0387654321",
            "0771234567",
            "0561234567",
            "0861234567",
            "0903123456",
            "0329876543"
    })
    @DisplayName("Should return true for valid Vietnamese phone numbers")
    void testValidPhoneNumber(String phone) {
        assertTrue(ValidationUtil.isValidPhoneNumber(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "1234567890",
            "0123456789",
            "0212345678",
            "0412345678",
            "0612345678",
            "09123",
            "09123456789",
            "091234567a",
            "abcdefghij",
            "09 12345678"
    })
    @DisplayName("Should return false for invalid phone numbers")
    void testInvalidPhoneNumber(String phone) {
        assertFalse(ValidationUtil.isValidPhoneNumber(phone));
    }

    @Test
    @DisplayName("Should return false when phone number is null")
    void testNullPhoneNumber() {
        assertFalse(ValidationUtil.isValidPhoneNumber(null));
    }
}
