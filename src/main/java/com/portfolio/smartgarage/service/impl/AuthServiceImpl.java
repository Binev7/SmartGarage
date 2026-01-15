package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.dto.auth.AuthResponseDto;
import com.portfolio.smartgarage.dto.auth.LoginRequestDto;
import com.portfolio.smartgarage.dto.auth.RegisterRequestDto;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.UserMapper;
import com.portfolio.smartgarage.model.Role;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.repository.UserRepository;
import com.portfolio.smartgarage.security.JwtUtils;
import com.portfolio.smartgarage.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email " + request.getEmail() + " is already in use.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String token = jwtUtils.generateToken(user.getEmail());

        return userMapper.toAuthResponse(user, token);
    }
}
