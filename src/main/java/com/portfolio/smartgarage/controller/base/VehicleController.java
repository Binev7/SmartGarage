package com.portfolio.smartgarage.controller.base;

import com.portfolio.smartgarage.dto.vehicle.*;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleResponseDto;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.security.annotation.IsEmployeeOrCustomer;
import com.portfolio.smartgarage.security.annotation.IsVehicleOwnerOrEmployee;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final ClientVehicleService clientVehicleService;


    @GetMapping("/brands")
    public ResponseEntity<List<BrandResponseDto>> getBrands() {
        return ResponseEntity.ok(vehicleService.getAllBrands());
    }

    @GetMapping("/brands/{brandId}/models")
    public ResponseEntity<List<ModelResponseDto>> getModelsByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(vehicleService.getModelsByBrand(brandId));
    }

    @GetMapping("/models/{modelId}/years")
    public ResponseEntity<List<Integer>> getYears(@PathVariable Long modelId) {
        return ResponseEntity.ok(vehicleService.getAvailableYearsForModel(modelId));
    }

    @GetMapping("/{id}")
    @IsVehicleOwnerOrEmployee
    public ResponseEntity<ClientVehicleResponseDto> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(clientVehicleService.getVehicleById(id));
    }

    @GetMapping("/my")
    @IsVehicleOwnerOrEmployee
    public ResponseEntity<List<ClientVehicleResponseDto>> getMyVehicles(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(clientVehicleService.getMyVehicles(user.getId()));
    }

    @PostMapping("/my/register")
    @IsEmployeeOrCustomer
    public ResponseEntity<ClientVehicleResponseDto> registerMyOwnVehicle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ClientVehicleRequestDto request) {

        return ResponseEntity.ok(clientVehicleService.registerVehicle(request, userDetails.getId()));
    }

    @DeleteMapping("/{id}")
    @IsVehicleOwnerOrEmployee
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        clientVehicleService.deleteVehicle(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}