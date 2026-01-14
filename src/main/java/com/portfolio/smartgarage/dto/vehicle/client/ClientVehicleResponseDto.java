package com.portfolio.smartgarage.dto.vehicle.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientVehicleResponseDto {
    private Long id;
    private String licensePlate;
    private String vin;

    private int year;
    private String modelName;
    private String brandName;

    private Long ownerId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmail;
    private LocalDateTime registeredAt;
}