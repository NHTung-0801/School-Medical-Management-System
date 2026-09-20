package com.medical.schoolMedical.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OtpService {
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, ResetTokenData> resetTokenStorage = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 3;
    private static final int RESET_TOKEN_VALIDITY_MINUTES = 5;

    @Getter
    private static class OtpData {
        private final String otp;
        private final long expiryTime;

        public OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    @Getter
    private static class ResetTokenData {
        private final String email;
        private final Long userId;
        private final long expiryTime;

        public ResetTokenData(String email, Long userId, long expiryTime) {
            this.email = email;
            this.userId = userId;
            this.expiryTime = expiryTime;
        }
    }

    private String generateRandomOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    public String generateOtp(String email) {
        String otp = generateRandomOtp();
        long expiryTime = System.currentTimeMillis() + OTP_VALIDITY_MINUTES * 60 * 1000L;
        otpStorage.put(email, new OtpData(otp, expiryTime));
        scheduler.schedule(() -> {
            OtpData current = otpStorage.get(email);
            if (current != null && System.currentTimeMillis() >= current.expiryTime) {
                otpStorage.remove(email);
            }
        }, OTP_VALIDITY_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);
        if (otpData == null || System.currentTimeMillis() > otpData.expiryTime) {
            return false;
        }
        if (otpData.otp.equals(otp)) {
            // Hủy mã OTP ngay lập tức sau khi xác thực thành công để chống Replay Attack
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

    // === QUẢN LÝ PASSWORD RESET TOKEN ===

    public String generateResetToken(String email, Long userId) {
        String token = UUID.randomUUID().toString();
        long expiryTime = System.currentTimeMillis() + RESET_TOKEN_VALIDITY_MINUTES * 60 * 1000L;
        resetTokenStorage.put(token, new ResetTokenData(email, userId, expiryTime));
        scheduler.schedule(() -> {
            ResetTokenData current = resetTokenStorage.get(token);
            if (current != null && System.currentTimeMillis() >= current.expiryTime) {
                resetTokenStorage.remove(token);
            }
        }, RESET_TOKEN_VALIDITY_MINUTES, TimeUnit.MINUTES);
        return token;
    }

    public boolean validateResetToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        ResetTokenData data = resetTokenStorage.get(token);
        if (data == null || System.currentTimeMillis() > data.expiryTime) {
            if (data != null) {
                resetTokenStorage.remove(token);
            }
            return false;
        }
        return true;
    }

    public Long getUserIdByResetToken(String token) {
        if (!validateResetToken(token)) {
            return null;
        }
        return resetTokenStorage.get(token).getUserId();
    }

    public void invalidateResetToken(String token) {
        if (token != null) {
            resetTokenStorage.remove(token);
        }
    }
}

