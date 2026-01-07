package com.portfolio.smartgarage.controller.customer;

import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/services")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerServiceController {

    private final ServiceService serviceService;

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getAllAvailableServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }
}
