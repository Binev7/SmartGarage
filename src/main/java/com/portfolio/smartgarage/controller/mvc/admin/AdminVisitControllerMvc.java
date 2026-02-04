package com.portfolio.smartgarage.controller.mvc.admin;

import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.model.VisitStatus;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class AdminVisitControllerMvc {

    private final VisitService visitService;
    private final ServiceService serviceService;
    private final VehicleService vehicleService;


    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("adminName", userDetails.getUsername());
        }
        model.addAttribute("visits", visitService.getAllVisits());
        return "employee/dashboard";
    }

    @GetMapping("/visits/new-customer")
    public String showNewCustomerForm(Model model) {
        if (!model.containsAttribute("newCustomerDto")) {
            model.addAttribute("newCustomerDto", new NewCustomerVisitDto());
        }

        model.addAttribute("brands", vehicleService.getAllBrands());

        model.addAttribute("availableServices", serviceService.getAllServices());

        return "employee/new-customer-visit";
    }

    @PostMapping("/visits/new-customer")
    public String registerNewCustomerVisit(@ModelAttribute("newCustomerDto") @Valid NewCustomerVisitDto dto,
                                           BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newCustomerDto", bindingResult);
            redirectAttributes.addFlashAttribute("newCustomerDto", dto);
            return "redirect:/employee/visits/new-customer";
        }

        try {
            VisitViewDto result = visitService.registerVisitForNewCustomer(dto);

            redirectAttributes.addFlashAttribute("message", "Success! Temp password for client: " + result.getAdditionalComments());
            return "redirect:/employee/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration error: " + e.getMessage());
            return "redirect:/employee/visits/new-customer";
        }
    }

    @PostMapping("/visits/{id}/status")
    public String updateVisitStatus(@PathVariable Long id,
                                    @RequestParam VisitStatus status,
                                    RedirectAttributes redirectAttributes) {
        try {
            visitService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Status updated to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating status: " + e.getMessage());
        }
        return "redirect:/employee/dashboard";
    }

    @PostMapping("/visits/delete/{id}")
    public String deleteVisit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            visitService.deleteVisit(id);
            redirectAttributes.addFlashAttribute("message", "Visit record deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/dashboard";
    }

    @GetMapping("/visits/report/{id}")
    public String viewReport(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("report", visitService.getAdminReport(id));
            return "employee/visit-report";
        } catch (Exception e) {
            return "redirect:/employee/dashboard";
        }
    }
}