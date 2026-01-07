package com.portfolio.smartgarage.controller.admin;

import com.portfolio.smartgarage.dto.vehicle.VehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.VehicleResponseDto;
import com.portfolio.smartgarage.dto.vehicle.VehicleSearchDto;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/vehicles")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class AdminVehicleController {
    private final VehicleService vehicleService;

    @GetMapping("/search")
    public ResponseEntity<Page<VehicleResponseDto>> search(
            @Valid VehicleSearchDto criteria,
            @PageableDefault(size = 10, sort = "brand") Pageable pageable) {
        return ResponseEntity.ok(vehicleService.searchVehicles(criteria, pageable));
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDto> createVehicle(
            @Valid @RequestBody VehicleRequestDto request,
            @RequestParam Long ownerId) {
        return ResponseEntity.ok(vehicleService.createVehicle(request, ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDto request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}