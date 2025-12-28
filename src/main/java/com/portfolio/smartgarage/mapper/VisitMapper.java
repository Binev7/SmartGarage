package com.portfolio.smartgarage.mapper;

import com.portfolio.smartgarage.dto.CreateVisitDto;
import com.portfolio.smartgarage.dto.VisitViewDto;
import com.portfolio.smartgarage.model.Visit;
import org.springframework.stereotype.Component;

@Component
public class VisitMapper {

    public Visit toEntity(CreateVisitDto dto) {
        return Visit.builder()
                .date(dto.getDate())
                .additionalComments(dto.getAdditionalComments())
                .build();
    }

    public VisitViewDto toDto(Visit visit) {
        return VisitViewDto.builder()
                .id(visit.getId())
                .date(visit.getDate())
                .additionalComments(visit.getAdditionalComments())
                .status(visit.getStatus())

                .userId(visit.getUser().getId())
                .username(visit.getUser().getUsername())
                .userEmail(visit.getUser().getEmail())
                .userPhoneNumber(visit.getUser().getPhoneNumber())

                .vehicleId(visit.getVehicle().getId())
                .vehicleBrand(visit.getVehicle().getBrand())
                .vehicleModel(visit.getVehicle().getModel())
                .vehicleYear(visit.getVehicle().getYear())
                .vehicleLicensePlate(visit.getVehicle().getLicensePlate())
                .build();
    }
}

