package com.portfolio.smartgarage.controller;

import com.portfolio.smartgarage.dto.VehicleRequestDto;
import com.portfolio.smartgarage.dto.VehicleResponseDto;
import com.portfolio.smartgarage.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Validated
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponseDto> createVehicle(
            @Valid @RequestBody VehicleRequestDto request,
            @RequestParam("userId") Long userId
    ) {
        VehicleResponseDto created = vehicleService.createVehicle(request, userId);
        return ResponseEntity
                .created(URI.create("/vehicles/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> getVehicleById(@PathVariable("id") Long id) {
        VehicleResponseDto dto = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/owner/{userId}")
    public ResponseEntity<List<VehicleResponseDto>> getAllVehiclesByOwner(@PathVariable("userId") Long userId) {
        List<VehicleResponseDto> list = vehicleService.getAllVehiclesByOwner(userId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable("id") Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
