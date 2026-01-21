package com.portfolio.smartgarage.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerMvc {

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("totalRepairs", "8K+");
        model.addAttribute("yearsExperience", "22+");
        model.addAttribute("satisfiedClients", "500+");
        model.addAttribute("successRate", "99%");

        return "index";
    }
}