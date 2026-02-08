package vn.codegym.BE_BookOnline.service;


public interface EmailService {
    void sendVerificationEmail(String email, String recipientName, String token);
    void sendAccountLockEmail(String email, String fullName, String reason);
    void sendAccountUnlockEmail(String email, String fullName);

    void sendForgotPasswordEmail(String email, String fullName, String token);
}
