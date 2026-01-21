package com.portfolio.smartgarage.controller.rest.base;

import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.security.annotation.IsEmployeeOrCustomer;
import com.portfolio.smartgarage.security.annotation.IsVisitOwnerOrEmployee;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
@IsEmployeeOrCustomer
@RequiredArgsConstructor
@Tag(name = "Visits", description = "Customer-facing endpoints for managing and viewing garage visits")
public class VisitController {

    private final VisitService visitService;

    @Operation(summary = "Check calendar availability", description = "Returns a map of dates and their availability status starting from the given date.")
    @GetMapping("/availability")
    public ResponseEntity<Map<LocalDate, String>> getCalendarAvailability(
            @Parameter(description = "Start date for availability check (ISO format: YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok(visitService.getCalendarAvailability(startDate));
    }

    @Operation(summary = "Get visit details", description = "Retrieves specific information about a visit. Prices can be converted to the requested currency.")
    @GetMapping("/{id}")
    @IsVisitOwnerOrEmployee
    public ResponseEntity<VisitViewDto> getVisitDetails(
            @Parameter(description = "ID of the visit") @PathVariable Long id,
            @Parameter(description = "Currency code for price conversion (e.g., BGN, EUR, USD)")
            @RequestParam(defaultValue = "BGN") String currency) {
        return ResponseEntity.ok(visitService.getVisitDetails(id, currency));
    }

    @Operation(summary = "Get my visit history", description = "Retrieves the history of all visits for the currently logged-in customer. Can be filtered by a specific vehicle.")
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<VisitViewDto>> getMyVisitHistory(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Optional vehicle ID to filter the history")
            @RequestParam(required = false) Long vehicleId) {

        return ResponseEntity.ok(visitService.getVisitsByUser(user.getId(), vehicleId));
    }

    @Operation(summary = "Cancel a visit", description = "Allows a customer or employee to cancel/remove a scheduled visit.")
    @DeleteMapping("/{id}")
    @IsVisitOwnerOrEmployee
    public ResponseEntity<Void> cancelVisit(
            @Parameter(description = "ID of the visit to be cancelled") @PathVariable Long id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
}