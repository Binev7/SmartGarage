package com.portfolio.smartgarage.controller.auth;

import com.portfolio.smartgarage.dto.auth.LoginRequestDto;
import com.portfolio.smartgarage.dto.auth.RegisterRequestDto;
import com.portfolio.smartgarage.dto.auth.AuthResponseDto;
import com.portfolio.smartgarage.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        String token = authService.login(request);
        AuthResponseDto response = AuthResponseDto.builder().token(token).build();
        return ResponseEntity.ok(response);
    }
}