package com.portfolio.smartgarage.controller.rest.admin;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitAdminReportDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.model.VisitStatus;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/visits")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
@Tag(name = "Admin Visits", description = "Endpoints for managing the lifecycle of garage visits and repair jobs")
public class AdminVisitController {

    private final VisitService visitService;

    @Operation(summary = "Register a new visit", description = "Creates a service visit record for an existing customer and their vehicle.")
    @PostMapping
    public ResponseEntity<VisitViewDto> registerVisit(@Valid @RequestBody CreateVisitDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.createVisit(dto));
    }

    @Operation(summary = "Register visit for new customer", description = "Performs a 'one-stop' registration: creates a new user, registers their vehicle, and opens a visit record.")
    @PostMapping("/new-customer")
    public ResponseEntity<VisitViewDto> registerVisitForNewCustomer(@Valid @RequestBody NewCustomerVisitDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.registerVisitForNewCustomer(dto));
    }

    @Operation(summary = "Get detailed visit report", description = "Retrieves a full administrative report for a specific visit, including services and totals.")
    @GetMapping("/report/{id}")
    public ResponseEntity<VisitAdminReportDto> getDetailedReport(
            @Parameter(description = "ID of the visit") @PathVariable Long id) {
        return ResponseEntity.ok(visitService.getAdminReport(id));
    }

    @Operation(summary = "Update visit status", description = "Transitions the visit through different stages (e.g., PENDING, IN_PROGRESS, COMPLETED).")
    @PatchMapping("/{id}/status")
    public ResponseEntity<VisitViewDto> updateStatus(
            @Parameter(description = "ID of the visit to update") @PathVariable Long id,
            @Parameter(description = "New status to be applied") @RequestParam VisitStatus status) {
        return ResponseEntity.ok(visitService.updateStatus(id, status));
    }

    @Operation(summary = "Delete a visit", description = "Permanently removes a visit record from the system.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(
            @Parameter(description = "ID of the visit to delete") @PathVariable Long id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
}