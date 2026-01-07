package com.portfolio.smartgarage.service.interfaces;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal getExchangeRate(String from, String to);
}
