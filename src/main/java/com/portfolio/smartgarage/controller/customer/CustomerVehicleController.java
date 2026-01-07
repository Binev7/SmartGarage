package com.portfolio.smartgarage.controller.customer;

import com.portfolio.smartgarage.dto.vehicle.VehicleResponseDto;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/vehicles")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Validated
public class CustomerVehicleController {
    private final VehicleService vehicleService;

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> getVehicleById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/my-cars")
    public ResponseEntity<List<VehicleResponseDto>> getMyCars(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(vehicleService.getAllVehiclesByOwner(user.getId()));
    }
}
