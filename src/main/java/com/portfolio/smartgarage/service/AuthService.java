package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.LoginRequestDto;
import com.portfolio.smartgarage.dto.RegisterRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    void register(RegisterRequestDto request);

    String login(LoginRequestDto request);
}

