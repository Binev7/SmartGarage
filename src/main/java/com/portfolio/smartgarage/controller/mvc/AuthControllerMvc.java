package com.portfolio.smartgarage.controller.mvc;

import com.portfolio.smartgarage.dto.auth.AuthResponseDto;
import com.portfolio.smartgarage.dto.auth.LoginRequestDto;
import com.portfolio.smartgarage.dto.auth.RegisterRequestDto;
import com.portfolio.smartgarage.dto.auth.ResetPasswordRequestDto;
import com.portfolio.smartgarage.helper.util.CookieUtils;
import com.portfolio.smartgarage.service.interfaces.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthControllerMvc {

    private final AuthService authService;

    @GetMapping("/auth/login")
    public String getLoginPage() {
        return "auth/login";
    }

    @PostMapping("/auth/login")
    public String loginUser(@ModelAttribute("loginRequest") @Valid LoginRequestDto request,
                            BindingResult bindingResult,
                            HttpServletResponse response,
                            Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            AuthResponseDto authResponse = authService.login(request);
            response.addCookie(CookieUtils.createJwtCookie(authResponse.getToken(), 24 * 60 * 60));
            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password.");
            return "auth/login";
        }
    }

    @GetMapping("/auth/register")
    public String getRegisterPage() {
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String registerUser(@ModelAttribute("registerRequest") @Valid RegisterRequestDto request,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please check the highlighted fields.");
            return "auth/register";
        }

        try {
            authService.register(request);

            return "redirect:/auth/login?registered=true";

        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/auth/forgot-password")
    public String getForgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/auth/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            authService.forgotPassword(email);
            model.addAttribute("message", "A reset link has been sent to your email. Please check your inbox.");
        } catch (Exception e) {
            model.addAttribute("error", "Email not found.");
        }
        return "auth/forgot-password";
    }

    @GetMapping("/auth/reset-password")
    public String showResetPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/auth/reset-password")
    public String handleResetPassword(@ModelAttribute ResetPasswordRequestDto request, Model model) {
        try {
            authService.resetPassword(request);

            return "redirect:/auth/login?resetSuccess=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", request.getToken());
            return "auth/reset-password";
        }
    }
}