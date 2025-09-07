package com.brainacad.ecs.service;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.brainacad.ecs.entity.User;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final Environment env;

    public EmailService(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    public void sendActivationEmail(User user, String activationLink) {
        String subject = "Account Activation";
        String text = String.format("Hello, %s!%n%nPlease activate your account by clicking the link below:%n%s%n%nIf you did not request this, please ignore this email.",
                user.getUsername(), activationLink);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(env.getProperty("spring.mail.username", "noreply@brainacad.com"));
            mailSender.send(message);
        }

    public void sendPasswordResetEmail(User user, String resetLink) {
        String subject = "Password Reset Request";
        String text = String.format("Hello, %s!%n%nTo reset your password, click the link below:%n%s%n%nIf you did not request this, just ignore this email.",
                user.getUsername(), resetLink);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(env.getProperty("spring.mail.username", "noreply@brainacad.com"));
        mailSender.send(message);
    }

    /**
     * Send simple email message
     * @param to recipient email
     * @param subject email subject
     * @param text email body text
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(env.getProperty("spring.mail.username", "noreply@brainacad.com"));
        mailSender.send(message);
    }
}