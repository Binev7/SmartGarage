package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.exception.ExternalServiceException;
import com.portfolio.smartgarage.service.interfaces.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final RestClient currencyRestClient;


    @Override
    @Cacheable(value = "exchangeRates", key = "{#from, #to}")
    public BigDecimal getExchangeRate(String from, String to) {
        if (from.equalsIgnoreCase(to)) {
            return BigDecimal.ONE;
        }

        try {
            ExchangeRateResponse response = currencyRestClient.get()
                    .uri("/pair/{from}/{to}", from, to)
                    .retrieve()
                    .onStatus(status -> status.isError(), (request, resp) -> {
                        throw new ExternalServiceException("Currency API error. Status: " + resp.getStatusCode());
                    })
                    .body(ExchangeRateResponse.class);

            if (response == null || response.conversion_rate() == null) {
                throw new ExternalServiceException("Invalid response from Currency API");
            }

            return response.conversion_rate();

        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("Currency API is unreachable: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalServiceException("Unexpected error during exchange rate fetch: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal convert(BigDecimal amount, String from, String to) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (from.equalsIgnoreCase(to)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal rate = getExchangeRate(from, to);

        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private record ExchangeRateResponse(String result, BigDecimal conversion_rate) {}
}