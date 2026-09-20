package com.medical.schoolMedical.modules.auth.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpServiceTest {

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService();
    }

    @Test
    @DisplayName("Should generate 6-digit numeric OTP")
    void testGenerateOtp() {
        String email = "nurse@school.edu.vn";
        String otp = otpService.generateOtp(email);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("^\\d{6}$"), "OTP must contain only 6 digits");
    }

    @Test
    @DisplayName("Should validate correct OTP and invalidate it immediately after success (prevent replay attack)")
    void testValidateOtpSuccess() {
        String email = "parent@gmail.com";
        String otp = otpService.generateOtp(email);

        boolean isValidFirstTime = otpService.validateOtp(email, otp);
        assertTrue(isValidFirstTime, "First validation must be successful");

        // Replay attack: using the same OTP again should fail
        boolean isValidSecondTime = otpService.validateOtp(email, otp);
        assertFalse(isValidSecondTime, "Second validation must fail because OTP was invalidated");
    }

    @Test
    @DisplayName("Should return false when OTP does not match")
    void testValidateOtpFailure() {
        String email = "parent@gmail.com";
        otpService.generateOtp(email);

        boolean isValid = otpService.validateOtp(email, "000000");
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false when verifying OTP for non-existent email")
    void testValidateOtpNonExistentEmail() {
        boolean isValid = otpService.validateOtp("unknown@domain.com", "123456");
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate valid reset token and retrieve correct userId")
    void testResetTokenLifecycle() {
        String email = "user@school.edu.vn";
        Long userId = 99L;

        String token = otpService.generateResetToken(email, userId);
        assertNotNull(token);

        assertTrue(otpService.validateResetToken(token));
        assertEquals(userId, otpService.getUserIdByResetToken(token));

        // Invalidate token
        otpService.invalidateResetToken(token);
        assertFalse(otpService.validateResetToken(token));
        assertNull(otpService.getUserIdByResetToken(token));
    }

    @Test
    @DisplayName("Should return false for null or empty reset token")
    void testInvalidResetToken() {
        assertFalse(otpService.validateResetToken(null));
        assertFalse(otpService.validateResetToken(""));
        assertFalse(otpService.validateResetToken("   "));
        assertFalse(otpService.validateResetToken("random-non-existent-token"));
    }
}
