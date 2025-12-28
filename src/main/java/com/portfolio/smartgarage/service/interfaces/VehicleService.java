package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.vehicle.VehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.VehicleResponseDto;

import java.util.List;

public interface VehicleService {

    VehicleResponseDto createVehicle(VehicleRequestDto request, Long userId);

    VehicleResponseDto getVehicleById(Long vehicleId);

    List<VehicleResponseDto> getAllVehiclesByOwner(Long userId);

    void deleteVehicle(Long vehicleId);
}
