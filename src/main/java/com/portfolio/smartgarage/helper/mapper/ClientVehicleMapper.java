package com.portfolio.smartgarage.helper.mapper;

import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleResponseDto;
import com.portfolio.smartgarage.model.ClientVehicle;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.model.Vehicle;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClientVehicleMapper {

    public ClientVehicle toEntity(ClientVehicleRequestDto dto, Vehicle catalogVehicle, User owner) {
        return ClientVehicle.builder()
                .vin(dto.getVin())
                .licensePlate(dto.getLicensePlate())
                .vehicle(catalogVehicle)
                .owner(owner)
                .registeredAt(LocalDateTime.now())
                .build();
    }

    public ClientVehicleResponseDto toDto(ClientVehicle cv) {
        return ClientVehicleResponseDto.builder()
                .id(cv.getId())
                .vin(cv.getVin())
                .licensePlate(cv.getLicensePlate())

                .year(cv.getVehicle().getYear())
                .modelName(cv.getVehicle().getModel().getName())
                .brandName(cv.getVehicle().getModel().getBrand().getName())
                .registeredAt(cv.getRegisteredAt())

                .ownerId(cv.getOwner().getId())
                .ownerFirstName(cv.getOwner().getFirstName())
                .ownerLastName(cv.getOwner().getLastName())
                .ownerEmail(cv.getOwner().getEmail())
                .build();
    }
}