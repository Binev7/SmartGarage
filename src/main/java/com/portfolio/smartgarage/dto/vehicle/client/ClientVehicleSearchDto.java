package com.portfolio.smartgarage.dto.vehicle.client;

import lombok.Data;

@Data
public class ClientVehicleSearchDto {
    private String brand;
    private String model;
    private Integer year;
    private String vin;
    private String licensePlate;
    private String ownerEmail;
    private String ownerName;
    private String ownerPhone;
}