package com.portfolio.smartgarage.controller.admin;

import com.portfolio.smartgarage.dto.service.ServiceRequestDto;
import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
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
public class AdminServiceController {
    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<ServiceResponseDto> create (@Valid @RequestBody ServiceRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.createService(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> update (@PathVariable Long id, @Valid @RequestBody ServiceRequestDto dto) {
        return ResponseEntity.ok(serviceService.updateService(id, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete (@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
