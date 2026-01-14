package com.portfolio.smartgarage.helper.mapper;

import com.portfolio.smartgarage.dto.vehicle.VehicleCatalogDto;
import com.portfolio.smartgarage.model.Model;
import com.portfolio.smartgarage.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity(VehicleCatalogDto dto, Model model) {
        return Vehicle.builder()
                .model(model)
                .year(dto.getYear())
                .build();
    }
}