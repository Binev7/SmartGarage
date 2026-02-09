package com.portfolio.smartgarage.controller.rest.auth;

import com.portfolio.smartgarage.dto.auth.*;
import com.portfolio.smartgarage.helper.constant.EmailConstants;
import com.portfolio.smartgarage.helper.util.CookieUtils;
import com.portfolio.smartgarage.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and password recovery")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User login", description = "Authenticates user credentials and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        AuthResponseDto authResponse = authService.login(request);

        response.addCookie(CookieUtils.createJwtCookie(authResponse.getToken(), 24 * 60 * 60));

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Logout user", description = "Clears the authentication cookie.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        response.addCookie(CookieUtils.clearCookie("jwt"));
        return ResponseEntity.ok("Logged out successfully");
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account.")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(summary = "Forgot password", description = "Initiates recovery by email.")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(EmailConstants.RESET_LINK_SENT_MESSAGE);
    }

    @Operation(summary = "Reset password", description = "Updates password via reset token.")
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        AuthResponseDto response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verify reset token", description = "Checks if the token is still valid.")
    @GetMapping("/reset-password")
    public ResponseEntity<String> showResetPasswordPage(
            @Parameter(description = "The token sent via email")
            @RequestParam String token) {
        return ResponseEntity.ok("Valid token. Please send a POST request with your new password and token: " + token);
    }
}