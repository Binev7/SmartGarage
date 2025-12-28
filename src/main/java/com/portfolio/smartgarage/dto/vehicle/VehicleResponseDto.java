package com.portfolio.smartgarage.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {
    private Long id;
    private String licensePlate;
    private String vin;
    private int year;
    private String model;
    private String brand;
    private Long ownerId;
    private String ownerEmail;
}

