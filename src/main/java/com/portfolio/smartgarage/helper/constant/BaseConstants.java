package com.portfolio.smartgarage.helper.constant;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public final class BaseConstants {

    public static final int MAX_DAILY_VISITS = 6;
    public static final int CALENDAR_DAYS_HORIZON = 14;
    public static final String BASE_CURRENCY = "EUR";
    public static final String SYSTEM_PASS_TEMPLATE = "[System] Generated Password: %s";
    public static final List<String> WORK_HOURS = List.of(
            "09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00"
    );
}
