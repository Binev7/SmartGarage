package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.dto.auth.*;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.UserMapper;
import com.portfolio.smartgarage.model.PasswordResetToken;
import com.portfolio.smartgarage.model.Role;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.repository.UserRepository;
import com.portfolio.smartgarage.security.JwtUtils;
import com.portfolio.smartgarage.service.interfaces.AuthService;
import com.portfolio.smartgarage.service.interfaces.EmailService;
import com.portfolio.smartgarage.service.interfaces.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordResetTokenService tokenService;

    @Value("${smartgarage.app.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public void register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email " + request.getEmail() + " is already in use.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        emailService.sendRegistrationConfirmation(user.getEmail(), user.getFirstName());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String token = jwtUtils.generateToken(user.getEmail());
        return userMapper.toAuthResponse(user, token);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(user -> {

            String token = tokenService.createTokenForUser(user);

            String resetLink = String.format("%s/auth/reset-password?token=%s", baseUrl, token);

            log.info("Password reset requested for user: {}", email);

            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetLink);

        }, () -> {
            log.warn("Password reset attempted for non-existent email: {}", email);
        });
    }

    @Override
    @Transactional
    public AuthResponseDto resetPassword(ResetPasswordRequestDto request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match!");
        }

        PasswordResetToken resetToken = tokenService.validateToken(request.getToken());

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenService.deleteToken(resetToken);

        String jwtToken = jwtUtils.generateToken(user.getEmail());
        return userMapper.toAuthResponse(user, jwtToken);
    }
}