package com.portfolio.smartgarage.controller.rest.base;

import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.security.annotation.IsEmployeeOrCustomer;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@IsEmployeeOrCustomer
@RequiredArgsConstructor
@Tag(name = "Services", description = "Endpoints for viewing available garage services, accessible by both customers and employees")
public class ServiceController {

    private final ServiceService serviceService;

    @Operation(summary = "Search services by name", description = "Finds services matching the provided name or keyword.")
    @GetMapping("/search")
    public ResponseEntity<List<ServiceResponseDto>> searchServices(
            @Parameter(description = "The name or keyword to search for")
            @RequestParam String name) {
        return ResponseEntity.ok(serviceService.searchServicesByName(name));
    }

    @Operation(summary = "Get service by ID", description = "Retrieves detailed information about a specific service.")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getServiceById(
            @Parameter(description = "The unique identifier of the service")
            @PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @Operation(summary = "Get all available services", description = "Retrieves a full list of all repair and maintenance services currently offered.")
    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getAllAvailableServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }
}