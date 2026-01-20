package com.portfolio.smartgarage.controller.auth;

import com.portfolio.smartgarage.dto.auth.*;
import com.portfolio.smartgarage.helper.constant.EmailConstants;
import com.portfolio.smartgarage.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and password recovery")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new user account in the system.")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(summary = "User login", description = "Authenticates user credentials and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Forgot password", description = "Initiates the password recovery process by sending a reset link to the user's email.")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(EmailConstants.RESET_LINK_SENT_MESSAGE);
    }

    @Operation(summary = "Reset password", description = "Updates the user's password using a valid reset token received via email.")
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        AuthResponseDto response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verify reset token", description = "Verifies if the password reset token is valid before allowing the user to see the reset form.")
    @GetMapping("/reset-password")
    public ResponseEntity<String> showResetPasswordPage(
            @Parameter(description = "The token sent to the user's email for verification")
            @RequestParam String token) {
        return ResponseEntity.ok("Please send a POST request to this same URL with your new password and this token: " + token);
    }
}