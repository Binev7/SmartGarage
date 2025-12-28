package com.portfolio.smartgarage.mapper;

import com.portfolio.smartgarage.dto.vehicle.VehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.VehicleResponseDto;
import com.portfolio.smartgarage.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity(VehicleRequestDto dto) {
        return Vehicle.builder()
                .licensePlate(dto.getLicensePlate())
                .vin(dto.getVin())
                .year(dto.getYear())
                .model(dto.getModel())
                .brand(dto.getBrand())
                .build();
    }

    public VehicleResponseDto toDto(Vehicle v) {
        return VehicleResponseDto.builder()
                .id(v.getId())
                .licensePlate(v.getLicensePlate())
                .vin(v.getVin())
                .year(v.getYear())
                .model(v.getModel())
                .brand(v.getBrand())
                .ownerId(v.getOwner() != null ? v.getOwner().getId() : null)
                .build();
    }
}
