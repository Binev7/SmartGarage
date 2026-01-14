package com.portfolio.smartgarage.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ModelRequestDto {
    @NotBlank(message = "Model name is required")
    @Size(min = 2, max = 50)
    private String name;

    @NotNull(message = "Brand ID is required")
    private Long brandId;
}
