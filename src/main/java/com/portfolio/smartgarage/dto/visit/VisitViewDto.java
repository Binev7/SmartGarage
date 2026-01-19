package com.portfolio.smartgarage.dto.visit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.smartgarage.dto.service.ServiceSummaryDto;
import com.portfolio.smartgarage.helper.constant.BaseConstants;
import com.portfolio.smartgarage.model.VisitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VisitViewDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date;

    private String additionalComments;
    private VisitStatus status;

    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleLicensePlate;

    private List<ServiceSummaryDto> services;
    private BigDecimal totalPrice;

    @Builder.Default
    private String currency = BaseConstants.BASE_CURRENCY;
}
