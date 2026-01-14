package com.portfolio.smartgarage.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandRequestDto {
    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 50)
    private String name;
}