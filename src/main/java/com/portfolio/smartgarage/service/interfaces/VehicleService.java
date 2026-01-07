package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.vehicle.VehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.VehicleResponseDto;
import com.portfolio.smartgarage.dto.vehicle.VehicleSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {

    Page<VehicleResponseDto> searchVehicles(VehicleSearchDto criteria, Pageable pageable);

    VehicleResponseDto createVehicle(VehicleRequestDto request, Long userId);

    VehicleResponseDto updateVehicle(Long vehicleId, VehicleRequestDto request);

    VehicleResponseDto getVehicleById(Long vehicleId);

    List<VehicleResponseDto> getAllVehiclesByOwner(Long userId);

    void deleteVehicle(Long vehicleId);
}
