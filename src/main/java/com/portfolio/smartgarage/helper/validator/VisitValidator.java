package com.portfolio.smartgarage.helper.validator;

import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.exception.InvalidDataException;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.repository.UserRepository;
import com.portfolio.smartgarage.repository.VehicleRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class VisitValidator {

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;


    public void validateDailyLimit(LocalDate date, int limit) {
        if (visitRepository.countByDate(date) >= limit) {
            throw new InvalidDataException("Daily limit of " + limit + " visits reached for " + date);
        }
    }

    public void validateNewCustomerUniqueness(NewCustomerVisitDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("A user with the provided email already exists.");
        }
        if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new ResourceAlreadyExistsException("A user with the provided phone number already exists.");
        }
        if (vehicleRepository.findByLicensePlate(dto.getLicensePlate()).isPresent()) {
            throw new ResourceAlreadyExistsException("Vehicle with the same license plate already exists.");
        }
        if (vehicleRepository.findByVin(dto.getVin()).isPresent()) {
            throw new ResourceAlreadyExistsException("Vehicle with the same VIN already exists.");
        }
    }

    public void validateVehicleOwnership(User user, Vehicle vehicle) {
        if (!vehicle.getOwner().getId().equals(user.getId())) {
            throw new InvalidDataException("The selected vehicle does not belong to the specified customer.");
        }
    }
}