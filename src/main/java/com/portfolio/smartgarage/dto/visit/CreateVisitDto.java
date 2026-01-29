package com.portfolio.smartgarage.dto.visit;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVisitDto {

    @NotNull(message = "Vehicle ID is required")
    private Long clientVehicleId; // Променено от vehicleId, за да съвпада със сървиса ти

    @NotNull(message = "Please select a date")
    @FutureOrPresent(message = "The visit date cannot be in the past") // По-добре от @Future, за да може и за днес
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    private String additionalComments;

    @NotEmpty(message = "At least one service must be selected") // Гарантира, че няма да пратят празен списък
    private List<Long> serviceIds;
}