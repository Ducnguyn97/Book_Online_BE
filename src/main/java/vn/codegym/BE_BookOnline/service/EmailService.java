package vn.codegym.BE_BookOnline.service;

public interface EmailService {
    void sendVerificationEmail(String email, String recipientName, String token);
}
