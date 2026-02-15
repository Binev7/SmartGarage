package com.portfolio.smartgarage.controller.mvc.base;

import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.smartgarage.helper.constant.WebConstants.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeControllerMvc {

    private final ServiceService serviceService;

    @GetMapping(PATH_HOME)
    public String index() {
        return VIEW_HOME;
    }

    @GetMapping(PATH_SERVICES)
    public String showServices(@RequestParam(required = false) String query,
                               @RequestParam(required = false, defaultValue = CURRENCY_BGN) String currency,
                               Model model) {

        List<ServiceResponseDto> rawServices = fetchServicesInternal(query);

        BigDecimal exchangeRate = calculateExchangeRate(currency);

        List<ServiceResponseDto> displayedServices = convertPricesForDisplay(rawServices, exchangeRate);

        populateModel(model, displayedServices, query, currency, exchangeRate);

        return VIEW_SERVICES;
    }

    @GetMapping(PATH_ABOUT)
    public String showAboutPage() {
        return VIEW_ABOUT;
    }

    private List<ServiceResponseDto> fetchServicesInternal(String query) {
        try {
            if (query != null && !query.isBlank()) {
                return serviceService.searchServicesByName(query);
            } else {
                return serviceService.getAllServices();
            }
        } catch (Exception e) {
            log.error(LOG_ERR_FETCH_SERVICES, query, e);
            return Collections.emptyList();
        }
    }

    private BigDecimal calculateExchangeRate(String targetCurrency) {
        if (CURRENCY_EUR.equals(targetCurrency)) {
            return BigDecimal.ONE.divide(RATE_EUR_VALUE, 4, RoundingMode.HALF_UP);
        } else if (CURRENCY_USD.equals(targetCurrency)) {
            return BigDecimal.ONE.divide(RATE_USD_VALUE, 4, RoundingMode.HALF_UP);
        }
        return BigDecimal.ONE;
    }

    private List<ServiceResponseDto> convertPricesForDisplay(List<ServiceResponseDto> services, BigDecimal rate) {
        if (rate.compareTo(BigDecimal.ONE) == 0 || services.isEmpty()) {
            return services;
        }

        return services.stream()
                .map(s -> {
                    ServiceResponseDto viewDto = new ServiceResponseDto();
                    viewDto.setId(s.getId());
                    viewDto.setName(s.getName());

                    BigDecimal originalPrice = s.getPrice() != null ? s.getPrice() : BigDecimal.ZERO;
                    viewDto.setPrice(originalPrice.multiply(rate));

                    return viewDto;
                })
                .collect(Collectors.toList());
    }

    private void populateModel(Model model, List<ServiceResponseDto> services, String query, String selectedCurrency, BigDecimal rate) {
        model.addAttribute(MODEL_ATTR_SERVICES, services);
        model.addAttribute(MODEL_ATTR_SEARCH_QUERY, query);
        model.addAttribute(MODEL_ATTR_CURRENCIES, SUPPORTED_CURRENCIES_LIST);
        model.addAttribute(MODEL_ATTR_SELECTED_CURRENCY, selectedCurrency);

        if (!selectedCurrency.equals(CURRENCY_BGN)) {
            model.addAttribute(MODEL_ATTR_RATE, rate);
        }
    }
}