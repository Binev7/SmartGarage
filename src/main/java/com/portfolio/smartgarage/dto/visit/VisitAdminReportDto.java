package com.portfolio.smartgarage.dto.visit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class VisitAdminReportDto extends VisitViewDto {

    private Long userId;
    private String username;
    private String userEmail;
    private String userPhoneNumber;

    private Long clientVehicleId;
    private String vehicleVin;
    private int vehicleYear;
}
