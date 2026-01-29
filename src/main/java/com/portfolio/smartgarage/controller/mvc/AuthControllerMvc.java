package com.portfolio.smartgarage.controller.mvc;

import com.portfolio.smartgarage.dto.auth.*;
import com.portfolio.smartgarage.helper.util.CookieUtils;
import com.portfolio.smartgarage.service.interfaces.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthControllerMvc {

    private final AuthService authService;

    @GetMapping("/auth/home")
    public String smartRedirect(Authentication authentication) {
        if (authentication == null) return "redirect:/auth/login";

        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (roles.contains("ROLE_EMPLOYEE")) {
            return "redirect:/employee/dashboard";
        } else if (roles.contains("ROLE_CUSTOMER")) {
            return "redirect:/customer/dashboard";
        }

        return "redirect:/";
    }

    @GetMapping("/auth/login")
    public String getLoginPage(@RequestParam(value = "error", required = false) String errorParam,
                               Model model) {

        if (errorParam != null) {
            model.addAttribute("error", "Invalid email or password.");
        }

        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequestDto());
        }
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
            return "redirect:/auth/home";

        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
            return "auth/login";
        }
    }

    @GetMapping("/auth/register")
    public String getRegisterPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequestDto());
        }
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String registerUser(@ModelAttribute("registerRequest") @Valid RegisterRequestDto request,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
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
    public String handleForgotPassword(@RequestParam("email") String email,
                                       RedirectAttributes redirectAttributes) {
        try {
            authService.forgotPassword(email);
            redirectAttributes.addFlashAttribute("message", "A reset link has been sent to your email.");
            return "redirect:/auth/forgot-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Email not found.");
            return "redirect:/auth/forgot-password";
        }
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