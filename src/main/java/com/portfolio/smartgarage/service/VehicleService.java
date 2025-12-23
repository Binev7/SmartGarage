package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.VehicleRequestDto;
import com.portfolio.smartgarage.dto.VehicleResponseDto;

import java.util.List;

public interface VehicleService {

    VehicleResponseDto createVehicle(VehicleRequestDto request, Long userId);

    VehicleResponseDto getVehicleById(Long vehicleId);

    List<VehicleResponseDto> getAllVehiclesByOwner(Long userId);

    void deleteVehicle(Long vehicleId);
}

