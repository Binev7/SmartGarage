package com.portfolio.smartgarage.controller.admin;

import com.portfolio.smartgarage.dto.vehicle.*;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleResponseDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleSearchDto;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vehicles")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class AdminVehicleController {

    private final ClientVehicleService clientVehicleService;
    private final VehicleService vehicleService;


    @GetMapping("/search")
    public ResponseEntity<Page<ClientVehicleResponseDto>> search(
            @Valid ClientVehicleSearchDto criteria,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(clientVehicleService.searchVehicles(criteria, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientVehicleResponseDto> getVehicleDetails(@PathVariable Long id) {
        return ResponseEntity.ok(clientVehicleService.getVehicleById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ClientVehicleResponseDto>> getAllVehicles(
            @PageableDefault(size = 20, sort = "registeredAt") Pageable pageable) {
        return ResponseEntity.ok(clientVehicleService.getAllVehicles(pageable));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ClientVehicleResponseDto>> getVehiclesByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(clientVehicleService.getMyVehicles(ownerId));
    }

    @PostMapping("/owner/{ownerId}/register")
    public ResponseEntity<ClientVehicleResponseDto> registerClientVehicle(
            @PathVariable Long ownerId,
            @Valid @RequestBody ClientVehicleRequestDto request) {

        return ResponseEntity.ok(clientVehicleService.registerVehicle(request, ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientVehicleResponseDto> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody ClientVehicleRequestDto request) {
        return ResponseEntity.ok(clientVehicleService.updateVehicle(id, request));
    }

    @PostMapping("/brands")
    public ResponseEntity<BrandResponseDto> createBrand(@Valid @RequestBody BrandRequestDto dto) {
        return ResponseEntity.ok(vehicleService.createBrand(dto));
    }

    @PostMapping("/models")
    public ResponseEntity<ModelResponseDto> createModel(@Valid @RequestBody ModelRequestDto dto) {
        return ResponseEntity.ok(vehicleService.createModel(dto));
    }

    @PostMapping("/catalog")
    public ResponseEntity<Void> addToCatalog(@Valid @RequestBody VehicleCatalogDto catalogDto) {
        vehicleService.addVehicleToCatalog(catalogDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/brands/{id}/archive")
    public ResponseEntity<Void> archiveBrand(@PathVariable Long id) {
        vehicleService.archiveBrand(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/models/{id}/archive")
    public ResponseEntity<Void> archiveModel(@PathVariable Long id) {
        vehicleService.archiveModel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/catalog/{id}/archive")
    public ResponseEntity<Void> archiveCatalogEntry(@PathVariable Long id) {
        vehicleService.archiveVehicleFromCatalog(id);
        return ResponseEntity.noContent().build();
    }
}