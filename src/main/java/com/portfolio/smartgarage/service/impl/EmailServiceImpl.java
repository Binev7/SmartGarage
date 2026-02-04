package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.helper.constant.EmailConstants;
import com.portfolio.smartgarage.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${smartgarage.mail.from}")
    private String fromEmail;

    @Async
    @Override
    public void sendWelcomeEmail(String to, String firstName, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(EmailConstants.WELCOME_SUBJECT);

        String content = String.format(
                EmailConstants.WELCOME_WITH_PASSWORD_TEMPLATE,
                firstName, rawPassword
        );

        message.setText(content);
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendRegistrationConfirmation(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(EmailConstants.WELCOME_SUBJECT);

        String content = String.format(
                EmailConstants.REGISTRATION_CONFIRMATION_TEMPLATE,
                firstName
        );

        message.setText(content);
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String firstName, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(EmailConstants.RESET_PASSWORD_SUBJECT);

        String content = String.format(
                EmailConstants.RESET_PASSWORD_TEMPLATE,
                firstName, resetLink
        );

        message.setText(content);
        mailSender.send(message);
    }
}