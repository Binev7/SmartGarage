package com.portfolio.smartgarage.controller.admin;

import com.portfolio.smartgarage.dto.service.ServiceRequestDto;
import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
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
@RequestMapping("/admin/services")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
@Tag(name = "Admin Services", description = "Endpoints for managing the garage service catalog")
public class AdminServiceController {

    private final ServiceService serviceService;

    @Operation(summary = "Create a new service", description = "Adds a new service to the price list.")
    @PostMapping
    public ResponseEntity<ServiceResponseDto> create(@Valid @RequestBody ServiceRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.createService(dto));
    }

    @Operation(summary = "Update service", description = "Updates details of an existing service by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> update(
            @Parameter(description = "ID of the service to update") @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDto dto) {
        return ResponseEntity.ok(serviceService.updateService(id, dto));
    }

    @Operation(summary = "Delete service", description = "Removes a service from the catalog by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the service to delete") @PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}