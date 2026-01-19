package com.portfolio.smartgarage.helper.constant;

public class EmailConstants {

    public static final String WELCOME_SUBJECT = "Welcome to Smart Garage!";

    public static final String WELCOME_WITH_PASSWORD_TEMPLATE =
            "Hello %s,\n\n" +
                    "Your account has been successfully created.\n" +
                    "You can log in to our system using the following temporary password:\n" +
                    "Password: %s\n\n" +
                    "Please change your password after your first login for security reasons.\n\n" +
                    "Best regards,\n" +
                    "The Smart Garage Team";

    public static final String REGISTRATION_CONFIRMATION_TEMPLATE =
            "Hello %s,\n\n" +
                    "Your account in Smart Garage has been successfully created.\n" +
                    "You can now log in using the email and password you provided during registration.\n\n" +
                    "Best regards,\n" +
                    "The Smart Garage Team";

    public static final String RESET_PASSWORD_SUBJECT = "Reset your password - Smart Garage";
    public static final String RESET_PASSWORD_TEMPLATE =
            "Hello %s,\n\n" +
                    "You requested to reset your password. Please click the link below to set a new one:\n" +
                    "Link: %s\n\n" +
                    "This link will expire in 15 minutes.\n\n" +
                    "If you did not request this, please ignore this email.";

    public static final String RESET_LINK_SENT_MESSAGE = "If an account with this email exists, a password reset link has been sent.";
    public static final String PASSWORDS_DO_NOT_MATCH_MESSAGE = "Passwords do not match!";
}
