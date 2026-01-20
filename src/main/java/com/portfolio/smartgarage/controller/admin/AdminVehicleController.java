package com.portfolio.smartgarage.controller.admin;

import com.portfolio.smartgarage.dto.vehicle.*;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleResponseDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleSearchDto;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Vehicles", description = "Endpoints for managing client vehicles, brands, models, and the general catalog")
public class AdminVehicleController {

    private final ClientVehicleService clientVehicleService;
    private final VehicleService vehicleService;

    @Operation(summary = "Search client vehicles", description = "Search for registered vehicles using filters like license plate, VIN, or owner.")
    @GetMapping("/search")
    public ResponseEntity<Page<ClientVehicleResponseDto>> search(
            @Valid ClientVehicleSearchDto criteria,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(clientVehicleService.searchVehicles(criteria, pageable));
    }

    @Operation(summary = "Get client vehicle details", description = "Retrieve full details for a specific client vehicle by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ClientVehicleResponseDto> getVehicleDetails(
            @Parameter(description = "ID of the client vehicle") @PathVariable Long id) {
        return ResponseEntity.ok(clientVehicleService.getVehicleById(id));
    }

    @Operation(summary = "Get all registered vehicles", description = "Retrieve a paginated list of all vehicles registered in the system.")
    @GetMapping
    public ResponseEntity<Page<ClientVehicleResponseDto>> getAllVehicles(
            @PageableDefault(size = 20, sort = "registeredAt") Pageable pageable) {
        return ResponseEntity.ok(clientVehicleService.getAllVehicles(pageable));
    }

    @Operation(summary = "Get vehicles by owner", description = "Retrieve all vehicles belonging to a specific owner.")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ClientVehicleResponseDto>> getVehiclesByOwner(
            @Parameter(description = "ID of the owner/user") @PathVariable Long ownerId) {
        return ResponseEntity.ok(clientVehicleService.getMyVehicles(ownerId));
    }

    @Operation(summary = "Register vehicle to owner", description = "Assign and register a vehicle from the catalog to a specific user.")
    @PostMapping("/owner/{ownerId}/register")
    public ResponseEntity<ClientVehicleResponseDto> registerClientVehicle(
            @Parameter(description = "ID of the owner/user") @PathVariable Long ownerId,
            @Valid @RequestBody ClientVehicleRequestDto request) {
        return ResponseEntity.ok(clientVehicleService.registerVehicle(request, ownerId));
    }

    @Operation(summary = "Update client vehicle", description = "Update details like license plate or VIN for a specific client vehicle.")
    @PutMapping("/{id}")
    public ResponseEntity<ClientVehicleResponseDto> updateVehicle(
            @Parameter(description = "ID of the client vehicle to update") @PathVariable Long id,
            @Valid @RequestBody ClientVehicleRequestDto request) {
        return ResponseEntity.ok(clientVehicleService.updateVehicle(id, request));
    }

    @Operation(summary = "Create vehicle brand", description = "Add a new vehicle brand (e.g., BMW, Audi) to the system.")
    @PostMapping("/brands")
    public ResponseEntity<BrandResponseDto> createBrand(@Valid @RequestBody BrandRequestDto dto) {
        return ResponseEntity.ok(vehicleService.createBrand(dto));
    }

    @Operation(summary = "Create vehicle model", description = "Add a new vehicle model associated with a brand.")
    @PostMapping("/models")
    public ResponseEntity<ModelResponseDto> createModel(@Valid @RequestBody ModelRequestDto dto) {
        return ResponseEntity.ok(vehicleService.createModel(dto));
    }

    @Operation(summary = "Add vehicle to catalog", description = "Add a specific year/model configuration to the general vehicle catalog.")
    @PostMapping("/catalog")
    public ResponseEntity<Void> addToCatalog(@Valid @RequestBody VehicleCatalogDto catalogDto) {
        vehicleService.addVehicleToCatalog(catalogDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Archive brand", description = "Deactivate a brand so it can no longer be used for new registrations.")
    @PatchMapping("/brands/{id}/archive")
    public ResponseEntity<Void> archiveBrand(
            @Parameter(description = "ID of the brand to archive") @PathVariable Long id) {
        vehicleService.archiveBrand(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Archive model", description = "Deactivate a specific model.")
    @PatchMapping("/models/{id}/archive")
    public ResponseEntity<Void> archiveModel(
            @Parameter(description = "ID of the model to archive") @PathVariable Long id) {
        vehicleService.archiveModel(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Archive catalog entry", description = "Deactivate a specific vehicle entry from the general catalog.")
    @PatchMapping("/catalog/{id}/archive")
    public ResponseEntity<Void> archiveCatalogEntry(
            @Parameter(description = "ID of the catalog entry to archive") @PathVariable Long id) {
        vehicleService.archiveVehicleFromCatalog(id);
        return ResponseEntity.noContent().build();
    }
}