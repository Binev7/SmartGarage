package com.portfolio.smartgarage.controller.rest.base;

import com.portfolio.smartgarage.dto.vehicle.*;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleResponseDto;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.security.annotation.IsEmployeeOrCustomer;
import com.portfolio.smartgarage.security.annotation.IsVehicleOwnerOrEmployee;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Endpoints for vehicle catalog lookup and personal vehicle management")
public class VehicleController {

    private final VehicleService vehicleService;
    private final ClientVehicleService clientVehicleService;

    @Operation(summary = "Get all brands", description = "Retrieves a list of all active vehicle brands in the catalog.")
    @GetMapping("/brands")
    public ResponseEntity<List<BrandResponseDto>> getBrands() {
        return ResponseEntity.ok(vehicleService.getAllBrands());
    }

    @Operation(summary = "Get models by brand", description = "Retrieves all available models for a specific brand ID.")
    @GetMapping("/brands/{brandId}/models")
    public ResponseEntity<List<ModelResponseDto>> getModelsByBrand(
            @Parameter(description = "ID of the vehicle brand") @PathVariable Long brandId) {
        return ResponseEntity.ok(vehicleService.getModelsByBrand(brandId));
    }

    @Operation(summary = "Get available years for model", description = "Retrieves a list of manufacturing years available in the catalog for a specific model.")
    @GetMapping("/models/{modelId}/years")
    public ResponseEntity<List<Integer>> getYears(
            @Parameter(description = "ID of the vehicle model") @PathVariable Long modelId) {
        return ResponseEntity.ok(vehicleService.getAvailableYearsForModel(modelId));
    }

    @Operation(summary = "Get vehicle details", description = "Retrieves details for a specific client vehicle. Restricted to owner or employees.")
    @GetMapping("/{id}")
    @IsVehicleOwnerOrEmployee
    public ResponseEntity<ClientVehicleResponseDto> getVehicleById(
            @Parameter(description = "ID of the registered client vehicle") @PathVariable Long id) {
        return ResponseEntity.ok(clientVehicleService.getVehicleById(id));
    }

    @Operation(summary = "Get my vehicles", description = "Retrieves a list of all vehicles registered to the currently authenticated user.")
    @GetMapping("/my")
    @IsVehicleOwnerOrEmployee
    public ResponseEntity<List<ClientVehicleResponseDto>> getMyVehicles(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(clientVehicleService.getMyVehicles(user.getId()));
    }

    @Operation(summary = "Register personal vehicle", description = "Allows a customer to register a vehicle from the catalog to their own profile.")
    @PostMapping("/my/register")
    @IsEmployeeOrCustomer
    public ResponseEntity<ClientVehicleResponseDto> registerMyOwnVehicle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ClientVehicleRequestDto request) {
        return ResponseEntity.ok(clientVehicleService.registerVehicle(request, userDetails.getId()));
    }

    @Operation(summary = "Delete vehicle", description = "Removes a vehicle from the user's profile. Restricted to owner or employees.")
    @DeleteMapping("/{id}")
    @IsVehicleOwnerOrEmployee
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "ID of the vehicle to delete") @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        clientVehicleService.deleteVehicle(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}