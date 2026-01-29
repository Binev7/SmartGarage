package com.portfolio.smartgarage.controller.mvc;

import com.portfolio.smartgarage.service.interfaces.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/customer/services")
@RequiredArgsConstructor
public class CustomerServiceControllerMvc {

    private final ServiceService serviceService;

    @GetMapping
    public String listServices(@RequestParam(required = false) String query, Model model) {
        if (query != null && !query.isBlank()) {
            model.addAttribute("services", serviceService.searchServicesByName(query));
            model.addAttribute("searchQuery", query);
        } else {
            model.addAttribute("services", serviceService.getAllServices());
        }
        return "customer/services";
    }
}