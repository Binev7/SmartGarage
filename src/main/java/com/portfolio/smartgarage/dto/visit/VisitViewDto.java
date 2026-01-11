package com.portfolio.smartgarage.dto.visit;

import com.portfolio.smartgarage.dto.service.ServiceSummaryDto;
import com.portfolio.smartgarage.model.VisitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitViewDto {

    private Long id;
    private LocalDateTime date;
    private String additionalComments;
    private VisitStatus status;


    private Long userId;
    private String username;
    private String userEmail;
    private String userPhoneNumber;


    private Long vehicleId;
    private String vehicleBrand;
    private String vehicleModel;
    private int vehicleYear;
    private String vehicleLicensePlate;


    private List<ServiceSummaryDto> services;
    private BigDecimal totalPrice;

    @Builder.Default
    private String currency = "BGN";
}
