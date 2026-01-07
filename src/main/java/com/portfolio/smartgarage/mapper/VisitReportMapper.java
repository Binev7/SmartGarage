package com.portfolio.smartgarage.mapper;

import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.model.Visit;
import com.portfolio.smartgarage.service.interfaces.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class VisitReportMapper {

    private final VisitMapper visitMapper; // Твоят оригинален мапер
    private final CurrencyService currencyService;

    public static final String DEFAULT_CURRENCY = "BGN";

    public VisitViewDto toViewDto(Visit visit) {
        return visitMapper.toDto(visit);
    }

    public VisitViewDto toViewDtoWithCurrency(Visit visit, String targetCurrency) {
        VisitViewDto dto = visitMapper.toDto(visit);

        if (targetCurrency != null && !DEFAULT_CURRENCY.equalsIgnoreCase(targetCurrency)) {
            BigDecimal rate = currencyService.getExchangeRate(DEFAULT_CURRENCY, targetCurrency);
            if (dto.getTotalPrice() != null) {
                dto.setTotalPrice(dto.getTotalPrice().multiply(rate));
            }
        }

        return dto;
    }
}