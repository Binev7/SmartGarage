package com.portfolio.smartgarage.controller.mvc;

import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.helper.constant.BaseConstants;
import com.portfolio.smartgarage.service.interfaces.CurrencyService;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer/services")
@RequiredArgsConstructor
public class CustomerServiceControllerMvc {

    private final ServiceService serviceService;
    private final CurrencyService currencyService;

    @GetMapping
    public String listServices(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "currency", defaultValue = BaseConstants.BASE_CURRENCY) String currencyCode,
            Model model) {

        List<ServiceResponseDto> services = serviceService.getAllServices();

        if (!BaseConstants.BASE_CURRENCY.equalsIgnoreCase(currencyCode)) {
            try {
                BigDecimal rate = currencyService.getExchangeRate(BaseConstants.BASE_CURRENCY, currencyCode);
                services = services.stream()
                        .map(s -> new ServiceResponseDto(s.getId(), s.getName(), s.getPrice().multiply(rate)))
                        .collect(Collectors.toList());
                model.addAttribute("rate", rate);
            } catch (Exception e) {
                model.addAttribute("error", "Conversion error. Showing prices in EUR.");
                currencyCode = BaseConstants.BASE_CURRENCY;
            }
        }

        model.addAttribute("services", services);
        model.addAttribute("searchQuery", query);
        model.addAttribute("selectedCurrency", currencyCode.toUpperCase());
        model.addAttribute("currencies", List.of("EUR", "BGN", "USD", "GBP"));

        return "customer/services";
    }
}