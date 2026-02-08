package vn.codegym.BE_BookOnline.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.support}")
    private String supportEmail;

    @Value("${app.name}")
    private String appName;

    @Value("${app.url}")
    private String appUrl;


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
            context.setVariable("currentYear", Year.now().getValue()); // Sử dụng biến Year

            String htmlContent = templateEngine.process("emails/email-verification", context);

            sendHtmlEmail(email,
                    "✅ Xác thực Email để kích hoạt tài khoản Book online",
                    htmlContent);

            log.info("Email kích hoạt thành công tới: {}", email);

        }catch (Exception e){
            throw new RuntimeException("Không thể gửi email kích hoạt." ,e);
        }
    }

    @Override
    public void sendAccountLockEmail(String email, String fullName, String reason) {
        try{
            Context context = new Context();
            context.setVariable("fullName", fullName != null? fullName: email);
            context.setVariable("reason", reason);
            context.setVariable("appName", appName); // Sử dụng biến appName nếu có
            context.setVariable("supportEmail", supportEmail); // Sử dụng biến supportEmail nếu có
            context.setVariable("currentDate", java.time.LocalDateTime.now());
            context.setVariable("currentYear", Year.now().getValue()); // Sử dụng biến Year
            context.setVariable("appUrl", appUrl);

            String htmlContent = templateEngine.process("emails/email-lock-account", context);

            sendHtmlEmail(email,
                    "⚠️ Thông báo khóa tài khoản Book online",
                    htmlContent);

            log.info("Email thông báo khóa tài khoản thành công tới: {}", email);
        }catch (Exception e){
            throw new RuntimeException("Không thể gửi email thông báo khóa tài khoản." ,e);
        }

    }

    @Override
    public void sendAccountUnlockEmail(String email, String fullName) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName != null ? fullName : email);
            context.setVariable("appName", appName); // Sử dụng biến appName nếu có
            context.setVariable("supportEmail", supportEmail); // Sử dụng biến supportEmail nếu có
            context.setVariable("currentDate", java.time.LocalDate.now());
            context.setVariable("currentYear", Year.now().getValue()); // Sử dụng biến Year
            context.setVariable("appUrl", appUrl);
            context.setVariable("loginUrl", appUrl + "/login");

            String htmlContent = templateEngine.process("emails/email-unlock-account", context);

            sendHtmlEmail(email,
                    "✅ Thông báo mở khóa tài khoản Book online",
                    htmlContent);

            log.info("Email thông báo mở khóa tài khoản thành công tới: {}", email);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email thông báo mở khóa tài khoản.", e);
        }
    }

    @Override
    public void sendForgotPasswordEmail(String email, String fullName, String token) {
        try{
            Context context = new Context();
            context.setVariable("fullName", fullName != null? fullName: email);
            // link đặt lại mật khẩu trỏ về backend endpoint "http://localhost:8080/api/auth/reset-password?token=" + token;
            // link đặt lại mật khẩu trỏ về frontend endpoint "http://localhost:5173/reset-password?token=" + token;
            String resetPasswordLink = "http://localhost:8080/api/users/reset-password?token=" + token;
            context.setVariable("resetPasswordLink", resetPasswordLink);
            context.setVariable("appName", appName); // Sử dụng biến appName nếu có
            context.setVariable("currentYear", Year.now().getValue()); // Sử dụng biến Year

            String htmlContent = templateEngine.process("emails/email-forgot-password", context);

            sendHtmlEmail(email,
                    "🔐 Yêu cầu đặt lại mật khẩu Book online",
                    htmlContent);

            log.info("Email đặt lại mật khẩu thành công tới: {}", email);
        }catch (Exception e){
            throw new RuntimeException("Không thể gửi email đặt lại mật khẩu." ,e);
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
