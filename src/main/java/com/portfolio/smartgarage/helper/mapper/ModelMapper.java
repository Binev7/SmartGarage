package com.portfolio.smartgarage.helper.mapper;

import com.portfolio.smartgarage.dto.vehicle.ModelRequestDto;
import com.portfolio.smartgarage.dto.vehicle.ModelResponseDto;
import com.portfolio.smartgarage.model.Brand;
import com.portfolio.smartgarage.model.Model;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public Model toEntity(ModelRequestDto dto, Brand brand) {
        Model model = new Model();
        model.setName(dto.getName());
        model.setBrand(brand);
        return model;
    }

    public ModelResponseDto toResponseDto(Model model) {
        return new ModelResponseDto(
                model.getId(),
                model.getName()
        );
    }
}