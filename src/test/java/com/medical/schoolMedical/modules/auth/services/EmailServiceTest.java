package com.medical.schoolMedical.modules.auth.services;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, templateEngine);
    }

    @Test
    @DisplayName("Should send email successfully when mail sender and sender email are configured")
    void testSendOtpEmail_Success() {
        emailService.setSenderEmail("school.medical@gmail.com");

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("common/otp-email"), any(Context.class)))
                .thenReturn("<html><body>OTP: 123456</body></html>");

        assertDoesNotThrow(() -> emailService.sendOtpEmail("parent@example.com", "123456"));

        verify(mailSender, times(1)).createMimeMessage();
        verify(templateEngine, times(1)).process(eq("common/otp-email"), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Should fallback to console log and not send when sender email is empty")
    void testSendOtpEmail_WhenSenderEmailEmpty_ShouldFallback() {
        emailService.setSenderEmail("");

        assertDoesNotThrow(() -> emailService.sendOtpEmail("parent@example.com", "123456"));

        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should fallback gracefully when mail sender is null")
    void testSendOtpEmail_WhenMailSenderNull_ShouldFallback() {
        EmailService nullSenderService = new EmailService(null, templateEngine);
        nullSenderService.setSenderEmail("school.medical@gmail.com");

        assertDoesNotThrow(() -> nullSenderService.sendOtpEmail("parent@example.com", "123456"));

        verifyNoInteractions(templateEngine);
    }

    @Test
    @DisplayName("Should catch exception and not crash when mail send fails")
    void testSendOtpEmail_WhenMailSendFails_ShouldHandleGracefully() {
        emailService.setSenderEmail("school.medical@gmail.com");

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("common/otp-email"), any(Context.class)))
                .thenReturn("<html><body>OTP: 123456</body></html>");
        doThrow(new MailSendException("SMTP server connection timeout")).when(mailSender).send(mimeMessage);

        assertDoesNotThrow(() -> emailService.sendOtpEmail("parent@example.com", "123456"));

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
