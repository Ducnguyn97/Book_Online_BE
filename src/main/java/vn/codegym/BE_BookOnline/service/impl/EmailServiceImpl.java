package vn.codegym.BE_BookOnline.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.codegym.BE_BookOnline.service.EmailService;

import java.time.Year;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSenderImpl mailSender;

    @Value("app.mail.from")
    private String fromEmail;

    @Value("app.mail.support")
    private String supportEmail;

    @Value("app.name")
    private String appName;

    @Value("app.url")
    private String appUrl;

    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendVerificationEmail(String email, String recipientName, String token) {
        try {
            Context context = new Context();
            context.setVariable("recipientName", recipientName != null? recipientName: email);
            // link kích hoạt trỏ về backend endpoint "http://localhost:8080/api/auth/activate?token=" + token;
            // link kích hoạt trỏ về frontend endpoint "http://localhost:5173/login?token=" + token;
            String verificationLink = "http://localhost:8080/api/users/activate?token=" + token;
            context.setVariable("verificationLink", verificationLink);
            context.setVariable("appName", appName); // Sử dụng biến appName nếu có
            context.setVariable("currentYear", String.valueOf(Year.now().getValue())); // Sử dụng biến Year

            String htmlContent = templateEngine.process("emails/email-verification", context);

            sendHtmlEmail(email,
                    "✅ Xác thực Email để kích hoạt tài khoản Book online",
                    htmlContent);

            log.info("Email kích hoạt thành công tới: {}", email);

        }catch (Exception e){
            throw new RuntimeException("Không thể gửi email kích hoạt." ,e);
        }
    }

    private void sendHtmlEmail(String email, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent,true);

        mailSender.send(message);
    }
}
