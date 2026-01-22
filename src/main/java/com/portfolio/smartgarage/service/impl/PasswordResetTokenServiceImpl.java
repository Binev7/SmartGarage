package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.model.PasswordResetToken;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.repository.PasswordResetTokenRepository;
import com.portfolio.smartgarage.service.interfaces.PasswordResetTokenService;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    @Override
    @Transactional
    public String createTokenForUser(User user) {
        tokenRepository.deleteByUser(user);
        tokenRepository.flush();

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        tokenRepository.save(resetToken);
        return token;
    }

    @Override
    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or non-existent token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new IllegalStateException("Token has expired.");
        }
        return resetToken;
    }

    @Override
    @Transactional
    public void deleteToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }

    @Scheduled(fixedRateString = "${smartgarage.token.cleanup.rate}")
    @Transactional
    @Override
    public void removeExpiredTokens() {
        tokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }
}