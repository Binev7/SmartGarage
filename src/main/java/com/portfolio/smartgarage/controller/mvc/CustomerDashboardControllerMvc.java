package com.portfolio.smartgarage.controller.mvc;

import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerDashboardControllerMvc {

    private final ClientVehicleService clientVehicleService;
    private final VisitService visitService;

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
                                org.springframework.ui.Model model) {

        model.addAttribute("vehicles", clientVehicleService.getMyVehicles(userDetails.getId()));

        model.addAttribute("visits", visitService.getVisitsByUser(userDetails.getId(), null));

        model.addAttribute("firstName", userDetails.getUser().getFirstName());

        return "customer/dashboard";
    }
}