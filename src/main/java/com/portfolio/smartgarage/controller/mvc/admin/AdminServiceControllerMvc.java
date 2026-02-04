package com.portfolio.smartgarage.controller.mvc.admin;

import com.portfolio.smartgarage.dto.service.ServiceRequestDto;
import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.helper.constant.BaseConstants;
import com.portfolio.smartgarage.service.interfaces.CurrencyService;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/employee/services")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class AdminServiceControllerMvc {

    private final ServiceService serviceService;
    private final CurrencyService currencyService;


    @GetMapping
    public String listServices(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(name = "currency", defaultValue = BaseConstants.BASE_CURRENCY) String currencyCode,
            Model model) {

        List<ServiceResponseDto> services;
        BigDecimal rate = BigDecimal.ONE;

        try {
            services = (search != null && !search.trim().isEmpty())
                    ? serviceService.searchServicesByName(search)
                    : serviceService.getAllServices();

            if (search != null) model.addAttribute("searchQuery", search);

            if (!BaseConstants.BASE_CURRENCY.equalsIgnoreCase(currencyCode)) {
                try {
                    rate = currencyService.getExchangeRate(BaseConstants.BASE_CURRENCY, currencyCode);
                } catch (Exception e) {
                    model.addAttribute("error", "Currency API error. Prices shown in EUR.");
                    currencyCode = BaseConstants.BASE_CURRENCY;
                }
            }
        } catch (Exception e) {
            services = new ArrayList<>();
            model.addAttribute("info", e.getMessage());
        }

        model.addAttribute("services", services);
        model.addAttribute("rate", rate);
        model.addAttribute("selectedCurrency", currencyCode.toUpperCase());
        model.addAttribute("currencies", List.of("EUR", "BGN", "USD", "GBP"));

        if (!model.containsAttribute("serviceRequest")) {
            model.addAttribute("serviceRequest", new ServiceRequestDto());
        }
        return "employee/services";
    }


    @PostMapping("/add")
    public String create(@ModelAttribute("serviceRequest") @Valid ServiceRequestDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.serviceRequest", bindingResult);
            redirectAttributes.addFlashAttribute("serviceRequest", dto);
            return "redirect:/employee/services";
        }
        try {
            serviceService.createService(dto);
            redirectAttributes.addFlashAttribute("message", "Service created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/services";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("serviceRequest") @Valid ServiceRequestDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid data for update.");
            return "redirect:/employee/services";
        }
        try {
            serviceService.updateService(id, dto);
            redirectAttributes.addFlashAttribute("message", "Service updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/services";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceService.deleteService(id);
            redirectAttributes.addFlashAttribute("message", "Service deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/services";
    }
}