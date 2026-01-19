package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.auth.AuthResponseDto;
import com.portfolio.smartgarage.dto.auth.LoginRequestDto;
import com.portfolio.smartgarage.dto.auth.RegisterRequestDto;
import com.portfolio.smartgarage.dto.auth.ResetPasswordRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    void register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);

    void forgotPassword(String email);

    AuthResponseDto resetPassword(ResetPasswordRequestDto request);


}
