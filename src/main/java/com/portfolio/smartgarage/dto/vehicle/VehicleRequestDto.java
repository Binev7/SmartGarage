package com.portfolio.smartgarage.dto.vehicle;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDto {

    @NotBlank
    private String licensePlate;

    @NotBlank
    @Size(min = 17, max = 17)
    private String vin;

    @Min(1887)
    private int year;

    @NotBlank
    private String model;

    @NotBlank
    private String brand;
}

