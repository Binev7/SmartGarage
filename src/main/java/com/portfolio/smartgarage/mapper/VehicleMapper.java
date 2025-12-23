package com.portfolio.smartgarage.mapper;

import com.portfolio.smartgarage.dto.VehicleRequestDto;
import com.portfolio.smartgarage.dto.VehicleResponseDto;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.model.User;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity(VehicleRequestDto dto) {
        if (dto == null) return null;
        Vehicle v = new Vehicle();
        v.setLicensePlate(dto.getLicensePlate());
        v.setVin(dto.getVin());
        v.setYear(dto.getYear());
        v.setModel(dto.getModel());
        v.setBrand(dto.getBrand());
        return v;
    }

    public VehicleResponseDto toDto(Vehicle vehicle) {
        if (vehicle == null) return null;
        VehicleResponseDto dto = VehicleResponseDto.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .vin(vehicle.getVin())
                .year(vehicle.getYear())
                .model(vehicle.getModel())
                .brand(vehicle.getBrand())
                .build();

        User owner = vehicle.getOwner();
        if (owner != null) {
            dto.setOwnerId(owner.getId());
            dto.setOwnerEmail(owner.getEmail());
        }

        return dto;
    }
}

