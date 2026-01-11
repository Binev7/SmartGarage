package com.portfolio.smartgarage.controller.admin;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/visits")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class AdminVisitController {
    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<VisitViewDto> registerVisit(@Valid @RequestBody CreateVisitDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.createVisit(dto));
    }

    @PostMapping("/new-customer")
    public ResponseEntity<VisitViewDto> registerVisitForNewCustomer(@Valid @RequestBody NewCustomerVisitDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.registerVisitForNewCustomer(dto));
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<VisitViewDto> getDetailedReport(
            @PathVariable Long id,
            @RequestParam(defaultValue = "BGN") String currency) {
        return ResponseEntity.ok(visitService.getVisitDetails(id, currency));
    }


}
