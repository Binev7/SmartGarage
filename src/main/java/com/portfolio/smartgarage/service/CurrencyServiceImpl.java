package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.exception.ExternalServiceException;
import com.portfolio.smartgarage.service.interfaces.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final RestClient currencyRestClient;

    @Override
    public BigDecimal getExchangeRate(String from, String to) {
        try {
            ExchangeRateResponse response = currencyRestClient.get()
                    .uri("/pair/{from}/{to}", from, to)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, resp) -> {
                                throw new ExternalServiceException("Currency API error: " + resp.getStatusCode());
                            })
                    .body(ExchangeRateResponse.class);

            if (response != null && response.conversion_rate() != null) {
                return response.conversion_rate();
            }
            throw new ExternalServiceException("Empty response from Currency API");

        } catch (Exception e) {
            throw new ExternalServiceException("Failed to fetch exchange rate: " + e.getMessage());
        }
    }

    private record ExchangeRateResponse(String result, BigDecimal conversion_rate) {}
}