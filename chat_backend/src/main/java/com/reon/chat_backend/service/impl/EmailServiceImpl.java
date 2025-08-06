package com.reon.chat_backend.service.impl;

import com.reon.chat_backend.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.emailSender}")
    private String emailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendWelcomeEmail(String recipient, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailSender);
        message.setTo(recipient);
        message.setSubject("Welcome to ChatSphere!");

        message.setText("Hi " + name + ",\n\n" +
                "Welcome to ChatSphere!\n\n" +
                "We're glad to have you on board. You can now connect with others through private and group chats, share thoughts in real-time, and enjoy seamless messaging.\n\n" +
                "If you have any questions or need help getting started, feel free to contact our support team.\n\n" +
                "Happy chatting!\n\n" +
                "Best regards,\n" +
                "The ChatSphere Team");

        mailSender.send(message);
    }

    @Override
    public void verificationOTP(String recipient, String name, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailSender);
        message.setTo(recipient);
        message.setSubject("ChatSphere Account Verification OTP");

        String emailBody = "Hello " + name + ",\n\n"
                + "Your One-Time Password (OTP) to verify your ChatSphere account is: " + otp + "\n"
                + "This OTP is valid for the next 15 minutes.\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Best regards,\n"
                + "The ChatSphere Team";


        message.setText(emailBody);
        mailSender.send(message);
    }
}
