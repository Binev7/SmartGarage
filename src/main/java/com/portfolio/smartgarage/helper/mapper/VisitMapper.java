package com.portfolio.smartgarage.helper.mapper;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.service.ServiceSummaryDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.dto.visit.VisitAdminReportDto;
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

    public Visit toEntity(NewCustomerVisitDto dto) {
        return Visit.builder()
                .date(dto.getDate())
                .additionalComments(dto.getAdditionalComments())
                .build();
    }

    public VisitViewDto toDto(Visit visit) {
        return VisitViewDto.builder()
                .id(visit.getId())
                .date(visit.getDate())
                .status(visit.getStatus())
                .additionalComments(visit.getAdditionalComments())

                .vehicleBrand(visit.getClientVehicle().getVehicle().getModel().getBrand().getName())
                .vehicleModel(visit.getClientVehicle().getVehicle().getModel().getName())
                .vehicleYear(visit.getClientVehicle().getVehicle().getYear())
                .vehicleLicensePlate(visit.getClientVehicle().getLicensePlate())

                .services(visit.getServices().stream()
                        .map(this::serviceToSummary)
                        .collect(Collectors.toList()))
                .totalPrice(visit.getTotalPrice())
                .build();
    }

    public VisitAdminReportDto toAdminReportDto(Visit visit) {
        return VisitAdminReportDto.builder()
                .id(visit.getId())
                .date(visit.getDate())
                .status(visit.getStatus())
                .additionalComments(visit.getAdditionalComments())
                .vehicleBrand(visit.getClientVehicle().getVehicle().getModel().getBrand().getName())
                .vehicleModel(visit.getClientVehicle().getVehicle().getModel().getName())
                .vehicleLicensePlate(visit.getClientVehicle().getLicensePlate())
                .services(visit.getServices().stream()
                        .map(this::serviceToSummary)
                        .collect(Collectors.toList()))
                .totalPrice(visit.getTotalPrice())

                .userId(visit.getUser().getId())
                .username(visit.getUser().getUsername())
                .userEmail(visit.getUser().getEmail())
                .userPhoneNumber(visit.getUser().getPhoneNumber())

                .clientVehicleId(visit.getClientVehicle().getId())
                .vehicleVin(visit.getClientVehicle().getVin())
                .vehicleYear(visit.getClientVehicle().getVehicle().getYear())
                .build();
    }

    private ServiceSummaryDto serviceToSummary(Service s) {
        return ServiceSummaryDto.builder()
                .id(s.getId())
                .name(s.getName())
                .price(s.getPrice())
                .build();
    }
}