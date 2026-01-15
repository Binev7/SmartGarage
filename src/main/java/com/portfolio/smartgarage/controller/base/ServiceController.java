package com.portfolio.smartgarage.controller.base;

import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.security.annotation.IsEmployeeOrCustomer;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@IsEmployeeOrCustomer
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;


    @GetMapping("/search")
    public ResponseEntity<List<ServiceResponseDto>> searchServices(@RequestParam String name) {
        return ResponseEntity.ok(serviceService.searchServicesByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getAllAvailableServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }
}