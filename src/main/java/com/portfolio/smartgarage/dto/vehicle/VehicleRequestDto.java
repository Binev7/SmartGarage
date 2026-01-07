package com.portfolio.smartgarage.dto.vehicle;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDto {

    @NotBlank(message = "License plate is required")
    @Pattern(
            regexp = "^(A|B|BH|BP|BT|C|CA|CB|CH|EB|G|H|K|KH|M|OB|P|PA|PB|PK|PP|S|SA|SM|T|TX|V|Y)\\d{4}[A-Z]{2}$",
            message = "License plate must be a valid Bulgarian format (e.g., CB1234AB)"
    )
    private String licensePlate;

    @NotBlank(message = "VIN is required")
    @Size(min = 17, max = 17, message = "Vehicle Identification Number (VIN) must be exactly 17 characters")
    private String vin;

    @NotNull(message = "Year of creation is required")
    @Min(value = 1887, message = "Year of creation must be greater than 1886")
    private Integer year;

    @NotBlank(message = "Model name is required")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 symbols")
    private String model;

    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 symbols")
    private String brand;
}

