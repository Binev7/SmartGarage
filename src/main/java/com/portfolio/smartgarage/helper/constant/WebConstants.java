package com.portfolio.smartgarage.helper.constant;

import java.math.BigDecimal;
import java.util.List;

public final class WebConstants {

    private WebConstants() {}

    public static final String CURRENCY_BGN = "BGN";
    public static final String CURRENCY_EUR = "EUR";
    public static final String CURRENCY_USD = "USD";


    public static final BigDecimal RATE_EUR_VALUE = new BigDecimal("1.95583");
    public static final BigDecimal RATE_USD_VALUE = new BigDecimal("1.82");

    public static final List<String> SUPPORTED_CURRENCIES_LIST = List.of(CURRENCY_BGN, CURRENCY_EUR, CURRENCY_USD);


    public static final String PATH_HOME = "/";
    public static final String PATH_SERVICES = "/services";
    public static final String PATH_ABOUT = "/about";


    public static final String VIEW_HOME = "public/index";
    public static final String VIEW_SERVICES = "public/services";
    public static final String VIEW_ABOUT = "public/about";


    public static final String MODEL_ATTR_SERVICES = "services";
    public static final String MODEL_ATTR_SEARCH_QUERY = "searchQuery";
    public static final String MODEL_ATTR_CURRENCIES = "currencies";
    public static final String MODEL_ATTR_SELECTED_CURRENCY = "selectedCurrency";
    public static final String MODEL_ATTR_RATE = "rate";


    public static final String LOG_ERR_FETCH_SERVICES = "Failed to fetch services for query: {}";
}