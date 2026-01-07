package com.portfolio.smartgarage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${smartgarage.currency.api.url}")
    private String baseUrl;

    @Value("${smartgarage.currency.api.key}")
    private String apiKey;

    @Bean
    public RestClient currencyRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl + apiKey)
                .build();
    }
}