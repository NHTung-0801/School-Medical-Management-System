package com.medical.schoolMedical.modules.auth.services;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender,
                        @Autowired SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    @Async
    public void sendOtpEmail(String to, String otp) {
        // Luôn in mã OTP ra console để phục vụ kiểm thử (Dev Mode)
        log.info("=================================================");
        log.info("📧 [DEV MODE OTP] Gửi tới: {} | MÃ OTP: {}", to, otp);
        log.info("=================================================");

        // Nếu chưa cấu hình email hoặc email rỗng, dừng lại tại đây (không gây crash)
        if (mailSender == null || senderEmail == null || senderEmail.isBlank()) {
            log.warn("⚠️ Chưa cấu hình Gmail trong application-dev.properties hoặc biến môi trường! Mã OTP đã được in ra console log ở trên.");
            return;
        }

        try {
            log.info("🚀 Đang gửi email OTP từ [{}] tới [{}] qua SMTP Gmail...", senderEmail, to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(senderEmail, "Hệ Thống Y Tế Học Đường - School Medical System");
            helper.setTo(to);
            helper.setSubject("Mã xác thực OTP - Quản lý Y tế Học đường");

            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("appName", "School Medical Management System");

            String htmlContent = templateEngine.process("common/otp-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("✅ Đã gửi email OTP thành công tới: {}", to);
        } catch (Exception e) {
            log.error("❌ Không thể gửi email OTP tới {}: {}. Vui lòng kiểm tra lại Gmail App Password.", to, e.getMessage());
            log.info("👉 Bạn vẫn có thể dùng mã OTP đã in ở console log: {}", otp);
        }
    }
}

