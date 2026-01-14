package com.portfolio.smartgarage.dto.vehicle.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientVehicleRequestDto {

    @NotBlank(message = "License plate is required")
    @Pattern(
            regexp = "^(A|B|BH|BP|BT|C|CA|CB|CH|EB|G|H|K|KH|M|OB|P|PA|PB|PK|PP|S|SA|SM|T|TX|V|Y)\\d{4}[A-Z]{2}$",
            message = "Invalid Bulgarian license plate format (e.g., CB1234AB)"
    )
    private String licensePlate;

    @NotBlank(message = "VIN is required")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    private String vin;

    @NotNull(message = "Please select a vehicle from the catalog")
    private Long vehicleId;
}