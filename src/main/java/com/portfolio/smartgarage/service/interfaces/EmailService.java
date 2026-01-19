package com.portfolio.smartgarage.service.interfaces;

public interface EmailService {

    void sendWelcomeEmail(String to, String firstName, String rawPassword);

    void sendRegistrationConfirmation(String to, String firstName);

    void sendPasswordResetEmail(String to, String firstName, String resetLink);
}