package com.portfolio.smartgarage.dto.vehicle;

import lombok.Data;

@Data
public class VehicleSearchDto {
    private String brand;
    private String model;
    private Integer year;
    private String ownerName;
    private String licensePlate;
}
