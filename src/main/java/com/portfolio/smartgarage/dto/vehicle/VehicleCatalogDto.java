package com.portfolio.smartgarage.dto.vehicle;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleCatalogDto {
    @NotNull(message = "Model ID is required")
    private Long modelId;

    @NotNull(message = "Year is required")
    @Min(value = 1887, message = "Year must be at least 1887")
    private Integer year;
}