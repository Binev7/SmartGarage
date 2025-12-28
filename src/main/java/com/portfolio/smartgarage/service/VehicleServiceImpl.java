package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.VehicleRequestDto;
import com.portfolio.smartgarage.dto.VehicleResponseDto;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.mapper.VehicleMapper;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.repository.UserRepository;
import com.portfolio.smartgarage.repository.VehicleRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;
    private final VisitRepository visitRepository;

    @Override
    @Transactional
    public VehicleResponseDto createVehicle(VehicleRequestDto request, Long userId) {
        vehicleRepository.findByLicensePlate(request.getLicensePlate()).ifPresent(v -> {
            throw new ResourceAlreadyExistsException("Vehicle with license plate " + request.getLicensePlate() + " already exists");
        });

        vehicleRepository.findByVin(request.getVin()).ifPresent(v -> {
            throw new ResourceAlreadyExistsException("Vehicle with VIN " + request.getVin() + " already exists");
        });

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setOwner(owner);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toDto(saved);
    }

    @Override
    public VehicleResponseDto getVehicleById(Long vehicleId) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + vehicleId + " not found"));
        return vehicleMapper.toDto(v);
    }

    @Override
    public List<VehicleResponseDto> getAllVehiclesByOwner(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream()
                .filter(v -> v.getOwner() != null && v.getOwner().getId().equals(owner.getId()))
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + vehicleId + " not found"));

        visitRepository.deleteAllByVehicleId(vehicleId);

        vehicleRepository.delete(v);
    }
}
