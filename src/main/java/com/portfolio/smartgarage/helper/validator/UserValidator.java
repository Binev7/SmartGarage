package com.portfolio.smartgarage.helper.validator;

import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private static final int MAX_DAILY_VISITS = 6;


    private final VisitValidator visitValidator;
    private final VehicleValidator vehicleValidator;

    public void validateRegistration(NewCustomerVisitDto dto) {
        visitValidator.validateDailyLimit(dto.getDate().toLocalDate(), MAX_DAILY_VISITS);
        visitValidator.validateNewCustomerUniqueness(dto);
        vehicleValidator.validateUniqueness(dto.getLicensePlate(), dto.getVin());
    }
}
