package com.portfolio.smartgarage.controller.publicapi;

import com.portfolio.smartgarage.dto.vehicle.VehicleResponseDto;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.security.annotation.IsEmployeeOrCustomer;
import com.portfolio.smartgarage.security.annotation.IsVehicleOwnerOrEmployee;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@IsEmployeeOrCustomer
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/{id}")
    @IsVehicleOwnerOrEmployee
    public ResponseEntity<VehicleResponseDto> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<VehicleResponseDto>> getMyVehicles(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(vehicleService.getAllVehiclesByOwner(user.getId()));
    }
}