package com.daniel4k.volunteersync.volunteersync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@volunteersync.com}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request - VolunteerSync");
        message.setText("Hello,\n\n" +
                "You requested to reset your password. Click the link below to reset it:\n\n" +
                "http://localhost:3000/reset-password?token=" + resetToken + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "VolunteerSync Team");
        
        mailSender.send(message);
    }

    public void send2FACode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your 2FA Code - VolunteerSync");
        message.setText("Hello,\n\n" +
                "Your two-factor authentication code is:\n\n" +
                code + "\n\n" +
                "This code will expire in 10 minutes.\n\n" +
                "If you didn't request this, please contact support immediately.\n\n" +
                "Best regards,\n" +
                "VolunteerSync Team");
        
        mailSender.send(message);
    }

    public void sendWelcomeEmail(String toEmail, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to VolunteerSync!");
        message.setText("Hello " + firstName + ",\n\n" +
                "Welcome to VolunteerSync! Your account has been successfully created.\n\n" +
                "You can now log in and start exploring volunteer opportunities.\n\n" +
                "Best regards,\n" +
                "VolunteerSync Team");
        
        mailSender.send(message);
    }
}