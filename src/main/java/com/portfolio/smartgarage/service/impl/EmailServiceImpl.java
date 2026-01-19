package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.helper.constant.EmailConstants;
import com.portfolio.smartgarage.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendWelcomeEmail(String to, String firstName, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
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
        message.setTo(to);
        message.setSubject(EmailConstants.RESET_PASSWORD_SUBJECT);
        message.setText(String.format(EmailConstants.RESET_PASSWORD_TEMPLATE, firstName, resetLink));
        mailSender.send(message);
    }
}