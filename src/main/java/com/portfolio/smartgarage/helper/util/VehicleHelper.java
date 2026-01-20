package com.portfolio.smartgarage.helper.util;

import com.portfolio.smartgarage.model.ClientVehicle;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class VehicleHelper {

    public String formatFullName(ClientVehicle cv) {
        if (cv == null || cv.getVehicle() == null) return "Unknown Vehicle";

        var vehicle = cv.getVehicle();
        var model = vehicle.getModel();
        var brand = model.getBrand();

        return String.format("%s %s (%d)",
                brand.getName(),
                model.getName(),
                vehicle.getYear());
    }

    public Double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }
}