package com.portfolio.smartgarage.controller.customer;

import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer/visits")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerVisitController {
    private final VisitService visitService;

    @GetMapping("/history")
    public ResponseEntity<List<VisitViewDto>> getMyVisitHistory(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long vehicleId) {
        return ResponseEntity.ok(visitService.getVisitsByUser(user.getId(), vehicleId));
    }

    @GetMapping("/availability")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<LocalDate, String>> getCalendarAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        return ResponseEntity.ok(visitService.getAvailabilityPlan(startDate));
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<VisitViewDto> getMyDetailedReport(
            @PathVariable Long id,
            @RequestParam(defaultValue = "BGN") String currency,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(visitService.getVisitDetailsForCustomer(id, user.getId(), currency));
    }
}
