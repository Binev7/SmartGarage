package com.portfolio.smartgarage.helper.mapper;

import com.portfolio.smartgarage.dto.vehicle.BrandRequestDto;
import com.portfolio.smartgarage.dto.vehicle.BrandResponseDto;
import com.portfolio.smartgarage.model.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public Brand toEntity(BrandRequestDto dto) {
        Brand brand = new Brand();
        brand.setName(dto.getName());
        return brand;
    }

    public BrandResponseDto toResponseDto(Brand brand) {
        return new BrandResponseDto(
                brand.getId(),
                brand.getName()
        );
    }
}