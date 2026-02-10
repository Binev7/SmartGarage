package com.portfolio.smartgarage.helper.util;

import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.helper.constant.BaseConstants;
import com.portfolio.smartgarage.helper.mapper.VisitMapper;
import com.portfolio.smartgarage.model.Visit;
import com.portfolio.smartgarage.repository.VisitRepository;
import com.portfolio.smartgarage.service.interfaces.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VisitHelper {


    private static final String BASE_CURRENCY = "EUR";


    private final CurrencyService currencyService;
    private final VisitMapper visitMapper;
    private final VisitRepository visitRepository;

    public void appendPasswordToComments(VisitViewDto response, String rawPassword) {
        String passwordMessage = String.format(BaseConstants.SYSTEM_PASS_TEMPLATE, rawPassword);
        String current = response.getAdditionalComments();
        response.setAdditionalComments((current == null || current.isBlank())
                ? passwordMessage : current + " " + passwordMessage);
    }

    public VisitViewDto mapWithCurrency(Visit visit, String targetCurrency) {
        VisitViewDto dto = visitMapper.toDto(visit);
        if (targetCurrency != null && !targetCurrency.isBlank() &&
                !targetCurrency.equalsIgnoreCase(BaseConstants.BASE_CURRENCY)) {
            BigDecimal convertedTotal = currencyService.convert(visit.getTotalPrice(), BASE_CURRENCY, targetCurrency);
            dto.setTotalPrice(convertedTotal);
            dto.setCurrency(targetCurrency.toUpperCase());
        } else {
            dto.setCurrency(BASE_CURRENCY);
        }
        return dto;
    }

    public Map<LocalDate, String> generateAvailabilityCalendar(LocalDate startDate, int horizon, int maxDailyVisits) {
        Map<LocalDate, String> calendar = new LinkedHashMap<>();
        for (int i = 0; i < horizon; i++) {
            LocalDate date = startDate.plusDays(i);
            long count = visitRepository.countByDate(date);
            String status = (count >= maxDailyVisits)
                    ? "FULL"
                    : "AVAILABLE (" + (maxDailyVisits - count) + ")";
            calendar.put(date, status);
        }
        return calendar;
    }

    public <T extends VisitViewDto> T applyCurrency(T dto, BigDecimal basePrice, String targetCurrency) {
        if (targetCurrency != null && !targetCurrency.isBlank() && !targetCurrency.equalsIgnoreCase(BaseConstants.BASE_CURRENCY)) {
            BigDecimal convertedTotal = currencyService.convert(basePrice, BaseConstants.BASE_CURRENCY, targetCurrency);
            dto.setTotalPrice(convertedTotal);
            dto.setCurrency(targetCurrency.toUpperCase());
        } else {
            dto.setCurrency(BaseConstants.BASE_CURRENCY);
        }
        return dto;
    }
}