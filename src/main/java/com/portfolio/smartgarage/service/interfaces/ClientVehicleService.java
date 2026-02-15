package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleResponseDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleSearchDto;
import com.portfolio.smartgarage.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientVehicleService {

    ClientVehicleResponseDto registerVehicle(ClientVehicleRequestDto request, Long userId);

    Page<ClientVehicleResponseDto> searchVehicles(ClientVehicleSearchDto criteria, Pageable pageable);

    ClientVehicleResponseDto getVehicleById(Long id);

    Page<ClientVehicleResponseDto> getAllVehicles(Pageable pageable);

    ClientVehicleResponseDto updateVehicle(Long id, ClientVehicleRequestDto request);

    List<ClientVehicleResponseDto> getMyVehicles(Long userId);

    void deleteVehicleByAdmin(Long id);
}