package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.model.PasswordResetToken;
import com.portfolio.smartgarage.model.User;

public interface PasswordResetTokenService {
    String createTokenForUser(User user);

    PasswordResetToken validateToken(String token);

    void deleteToken(PasswordResetToken token);

    void removeExpiredTokens();
}