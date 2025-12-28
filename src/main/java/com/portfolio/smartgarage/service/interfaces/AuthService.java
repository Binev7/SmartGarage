package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.auth.LoginRequestDto;
import com.portfolio.smartgarage.dto.auth.RegisterRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    void register(RegisterRequestDto request);

    String login(LoginRequestDto request);
}
