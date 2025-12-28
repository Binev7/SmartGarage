package com.portfolio.smartgarage.mapper;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.service.ServiceSummaryDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.model.Service;
import com.portfolio.smartgarage.model.Visit;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class VisitMapper {

    public Visit toEntity(CreateVisitDto dto) {
        return Visit.builder()
                .date(dto.getDate())
                .additionalComments(dto.getAdditionalComments())
                .build();
    }

    public VisitViewDto toDto(Visit visit) {
        VisitViewDto.VisitViewDtoBuilder builder = VisitViewDto.builder()
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
                .totalPrice(visit.getTotalPrice());

        if (visit.getServices() != null && !visit.getServices().isEmpty()) {
            builder.services(visit.getServices().stream().map(this::serviceToSummary).collect(Collectors.toList()));
        }

        return builder.build();
    }

    private ServiceSummaryDto serviceToSummary(Service s) {
        return ServiceSummaryDto.builder()
                .id(s.getId())
                .name(s.getName())
                .price(s.getPrice())
                .build();
    }
}
