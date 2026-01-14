package com.portfolio.smartgarage.helper.validator;

import com.portfolio.smartgarage.exception.InvalidDataException;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.model.ClientVehicle;
import com.portfolio.smartgarage.model.VisitStatus;
import com.portfolio.smartgarage.repository.ClientVehicleRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VehicleValidator {

    private final ClientVehicleRepository clientVehicleRepository;
    private final VisitRepository visitRepository;

    public void validateUniqueness(String plate, String vin) {
        if (clientVehicleRepository.findByLicensePlate(plate).isPresent()) {
            throw new ResourceAlreadyExistsException("License plate '" + plate + "' is already registered.");
        }
        if (clientVehicleRepository.findByVin(vin).isPresent()) {
            throw new ResourceAlreadyExistsException("VIN '" + vin + "' is already registered.");
        }
    }

    public void validateUniquenessForUpdate(Long currentId, String plate, String vin) {
        clientVehicleRepository.findByLicensePlate(plate)
                .ifPresent(v -> {
                    if (!v.getId().equals(currentId)) {
                        throw new ResourceAlreadyExistsException("License plate '" + plate + "' is already taken by another vehicle.");
                    }
                });

        clientVehicleRepository.findByVin(vin)
                .ifPresent(v -> {
                    if (!v.getId().equals(currentId)) {
                        throw new ResourceAlreadyExistsException("VIN '" + vin + "' is already taken by another vehicle.");
                    }
                });
    }

    public void validateDeletionAllowed(ClientVehicle vehicle) {
        List<VisitStatus> activeStatuses = List.of(VisitStatus.PENDING, VisitStatus.APPROVED);

        boolean hasActiveVisit = visitRepository.existsByClientVehicleIdAndStatusIn(
                vehicle.getId(),
                activeStatuses
        );

        if (hasActiveVisit) {
            throw new InvalidDataException("Vehicle with ID " + vehicle.getId() + " cannot be deleted because it has active visits.");
        }
    }
}