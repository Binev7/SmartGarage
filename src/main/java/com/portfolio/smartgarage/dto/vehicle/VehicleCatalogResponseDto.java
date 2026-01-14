package com.portfolio.smartgarage.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleCatalogResponseDto {
    private Long id;
    private String brandName;
    private String modelName;
    private int year;
}