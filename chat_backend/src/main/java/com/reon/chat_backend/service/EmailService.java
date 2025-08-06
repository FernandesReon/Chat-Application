package com.reon.chat_backend.service;

public interface EmailService {
    void sendWelcomeEmail(String recipient, String name);
    void verificationOTP(String recipient, String name, String otp);
}
