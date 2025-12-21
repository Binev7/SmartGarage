package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.LoginRequest;
import com.portfolio.smartgarage.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    void register(RegisterRequest request);

    String login(LoginRequest request);
}

