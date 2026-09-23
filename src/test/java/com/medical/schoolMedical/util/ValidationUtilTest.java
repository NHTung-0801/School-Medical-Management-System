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

    @ParameterizedTest
    @ValueSource(strings = {
            "Nguyễn Văn An",
            "Trần Thị Mai Phương",
            "Lê Hoàng Đức",
            "Đặng Quốc Hưng",
            "Vũ Thị Ánh Tuyết",
            "Jean-Luc Picard",
            "Mary Jane",
            "O'Connor",
            "Dr. Nguyễn"
    })
    @DisplayName("Should return true for valid Vietnamese and international full names")
    void testValidFullName(String name) {
        assertTrue(ValidationUtil.isValidFullName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "A",
            "Nguyễn #1",
            "Trần Văn A?",
            "Lê Văn @Hải",
            "Học sinh 123",
            "Student <script>",
            "Nguyễn/Văn/A",
            "Tên_có_gạch_dưới"
    })
    @DisplayName("Should return false for invalid full names with special characters or numbers")
    void testInvalidFullName(String name) {
        assertFalse(ValidationUtil.isValidFullName(name));
    }

    @Test
    @DisplayName("Should return false when full name is null or exceeds 100 chars")
    void testNullAndOverlongFullName() {
        assertFalse(ValidationUtil.isValidFullName(null));
        String overlongName = "A".repeat(101);
        assertFalse(ValidationUtil.isValidFullName(overlongName));
    }

    @Test
    @DisplayName("Should sanitize full name by stripping ?, #, @, digits, and extra spaces")
    void testSanitizeFullName() {
        assertEquals("Nguyễn Văn An", ValidationUtil.sanitizeFullName("   Nguyễn   Văn    An   "));
        assertEquals("Trần Văn B", ValidationUtil.sanitizeFullName("Trần #Văn ?B"));
        assertEquals("Lê Hoàng C", ValidationUtil.sanitizeFullName("Lê Hoàng C #1"));
        assertEquals("Vũ Thị Ánh", ValidationUtil.sanitizeFullName("Vũ Thị Ánh 123@@!!"));
        assertEquals("", ValidationUtil.sanitizeFullName(null));
        assertEquals("", ValidationUtil.sanitizeFullName("   ### ??? 123   "));
    }

    @Test
    @DisplayName("Should normalize full name into clean Title Case and strip invalid characters")
    void testNormalizeFullName() {
        assertEquals("Nguyễn Văn An", ValidationUtil.normalizeFullName("nguyễn văn an"));
        assertEquals("Trần Thị Mai", ValidationUtil.normalizeFullName("  TRẦN   THỊ   MAI  "));
        assertEquals("Đặng Quốc Toàn", ValidationUtil.normalizeFullName("đặng quốc toàn #1"));
        assertEquals("Vũ Đình Long", ValidationUtil.normalizeFullName("VŨ ĐÌNH LONG"));
        assertEquals("", ValidationUtil.normalizeFullName(null));
        assertEquals("", ValidationUtil.normalizeFullName("   "));
    }
}
