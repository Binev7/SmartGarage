package com.portfolio.smartgarage.controller.publicapi;

import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.security.annotation.IsEmployeeOrCustomer;
import com.portfolio.smartgarage.security.annotation.IsVisitOwnerOrEmployee;
import com.portfolio.smartgarage.service.interfaces.VisitService;
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
@RequestMapping("/visits")
@IsEmployeeOrCustomer
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @GetMapping("/availability")
    public ResponseEntity<Map<LocalDate, String>> getCalendarAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok(visitService.getCalendarAvailability(startDate));
    }

    @GetMapping("/{id}")
    @IsVisitOwnerOrEmployee
    public ResponseEntity<VisitViewDto> getVisitDetails(
            @PathVariable Long id,
            @RequestParam(defaultValue = "BGN") String currency) {

        return ResponseEntity.ok(visitService.getVisitDetails(id, currency));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<VisitViewDto>> getMyVisitHistory(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long vehicleId) {

        return ResponseEntity.ok(visitService.getVisitsByUser(user.getId(), vehicleId));
    }
}