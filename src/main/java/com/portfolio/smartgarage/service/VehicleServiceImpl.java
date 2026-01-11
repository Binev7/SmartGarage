package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.vehicle.VehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.VehicleResponseDto;
import com.portfolio.smartgarage.dto.vehicle.VehicleSearchDto;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.VehicleMapper;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.repository.UserRepository;
import com.portfolio.smartgarage.repository.VehicleRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import com.portfolio.smartgarage.repository.specifications.VehicleSpecifications;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Page<VehicleResponseDto> searchVehicles(VehicleSearchDto criteria, Pageable pageable) {
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecifications.hasBrand(criteria.getBrand()))
                .and(VehicleSpecifications.hasModel(criteria.getModel()))
                .and(VehicleSpecifications.hasYear(criteria.getYear()))
                .and(VehicleSpecifications.hasLicensePlate(criteria.getLicensePlate()))
                .and(VehicleSpecifications.hasOwnerName(criteria.getOwnerName()));

        Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);

        return vehiclePage.map(vehicleMapper::toDto);
    }

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

    public VehicleResponseDto updateVehicle(Long vehicleId, VehicleRequestDto request) {
        Vehicle existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + vehicleId + " not found"));
        if (!existing.getLicensePlate().equalsIgnoreCase(request.getLicensePlate())) {
            vehicleRepository.findByLicensePlate(request.getLicensePlate()).ifPresent(v -> {
                throw new ResourceAlreadyExistsException("Vehicle with license plate " + request.getLicensePlate() + " already exists");
            });
        }
        if (!existing.getVin().equalsIgnoreCase(request.getVin())) {
            vehicleRepository.findByVin(request.getVin()).ifPresent(v -> {
                throw new ResourceAlreadyExistsException("Vehicle with VIN " + request.getVin() + " already exists");
            });
        }

        existing.setBrand(request.getBrand());
        existing.setModel(request.getModel());
        existing.setYear(request.getYear());
        existing.setLicensePlate(request.getLicensePlate());
        existing.setVin(request.getVin());

        Vehicle updated = vehicleRepository.save(existing);
        return vehicleMapper.toDto(updated);
    }

    @Override
    public VehicleResponseDto getVehicleById(Long vehicleId) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + vehicleId + " not found"));
        return vehicleMapper.toDto(v);
    }

    @Override
    public List<VehicleResponseDto> getAllVehiclesByOwner(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }

        return vehicleRepository.findAllByOwnerId(userId).stream()
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
