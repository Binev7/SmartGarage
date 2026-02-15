package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.dto.vehicle.client.*;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.ClientVehicleMapper;
import com.portfolio.smartgarage.helper.validator.VehicleValidator;
import com.portfolio.smartgarage.model.ClientVehicle;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.repository.ClientVehicleRepository;
import com.portfolio.smartgarage.repository.UserRepository;
import com.portfolio.smartgarage.repository.VehicleRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import com.portfolio.smartgarage.repository.specifications.ClientVehicleSpecifications;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientVehicleServiceImpl implements ClientVehicleService {

    private final ClientVehicleRepository clientVehicleRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ClientVehicleMapper clientVehicleMapper;
    private final VisitRepository visitRepository;
    private final VehicleValidator vehicleValidator;

    @Override
    @Transactional
    public ClientVehicleResponseDto registerVehicle(ClientVehicleRequestDto request, Long userId) {
        vehicleValidator.validateUniqueness(request.getLicensePlate(), request.getVin());

        Vehicle catalogVehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Model not found in catalog"));

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ClientVehicle clientVehicle = clientVehicleMapper.toEntity(request, catalogVehicle, owner);
        ClientVehicle saved = clientVehicleRepository.save(clientVehicle);

        return clientVehicleMapper.toDto(saved);
    }

    @Override
    public Page<ClientVehicleResponseDto> searchVehicles(ClientVehicleSearchDto criteria, Pageable pageable) {
        Specification<ClientVehicle> spec = Specification
                .where(ClientVehicleSpecifications.hasBrand(criteria.getBrand()))
                .and(ClientVehicleSpecifications.hasLicensePlate(criteria.getLicensePlate()))
                .and(ClientVehicleSpecifications.hasVin(criteria.getVin()))
                .and(ClientVehicleSpecifications.hasOwnerEmail(criteria.getOwnerEmail()))
                .and(ClientVehicleSpecifications.hasOwnerName(criteria.getOwnerName()))
                .and(ClientVehicleSpecifications.hasOwnerPhone(criteria.getOwnerPhone()));

        return clientVehicleRepository.findAll(spec, pageable)
                .map(clientVehicleMapper::toDto);
    }

    @Override
    public ClientVehicleResponseDto getVehicleById(Long id) {
        ClientVehicle cv = clientVehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        return clientVehicleMapper.toDto(cv);
    }

    @Override
    public Page<ClientVehicleResponseDto> getAllVehicles(Pageable pageable) {
        return clientVehicleRepository.findAll(pageable)
                .map(clientVehicleMapper::toDto);
    }

    @Override
    @Transactional
    public ClientVehicleResponseDto updateVehicle(Long id, ClientVehicleRequestDto request) {
        ClientVehicle existing = clientVehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!existing.getLicensePlate().equals(request.getLicensePlate()) ||
                !existing.getVin().equals(request.getVin())) {
            vehicleValidator.validateUniquenessForUpdate(id, request.getLicensePlate(), request.getVin());        }

        if (!existing.getVehicle().getId().equals(request.getVehicleId())) {
            Vehicle newCatalogVehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Model not found"));
            existing.setVehicle(newCatalogVehicle);
        }

        existing.setLicensePlate(request.getLicensePlate());
        existing.setVin(request.getVin());

        return clientVehicleMapper.toDto(clientVehicleRepository.save(existing));
    }

    @Override
    public List<ClientVehicleResponseDto> getMyVehicles(Long userId) {
        return clientVehicleRepository.findAllByOwnerId(userId).stream()
                .map(clientVehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVehicleByAdmin(Long id) {
        ClientVehicle vehicle = clientVehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        visitRepository.deleteAllByClientVehicleId(id);
        clientVehicleRepository.delete(vehicle);
    }
}