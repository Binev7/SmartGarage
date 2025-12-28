package com.portfolio.smartgarage.mapper;

import com.portfolio.smartgarage.dto.service.ServiceRequestDto;
import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.model.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public Service toEntity(ServiceRequestDto dto) {
        return Service.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .build();
    }

    public ServiceResponseDto toDto(Service service) {
        return ServiceResponseDto.builder()
                .id(service.getId())
                .name(service.getName())
                .price(service.getPrice())
                .build();
    }
}
