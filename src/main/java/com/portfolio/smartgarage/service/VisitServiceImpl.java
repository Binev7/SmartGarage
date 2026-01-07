package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.vehicle.VehicleRequestDto;
import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.exception.InvalidDataException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.mapper.VehicleMapper;
import com.portfolio.smartgarage.mapper.VisitMapper;
import com.portfolio.smartgarage.mapper.VisitReportMapper;
import com.portfolio.smartgarage.model.*;
import com.portfolio.smartgarage.repository.*;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import com.portfolio.smartgarage.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final VisitReportMapper visitReportMapper;
    private final VisitMapper visitMapper;
    private final VehicleMapper vehicleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public VisitViewDto createVisit(CreateVisitDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + dto.getUserId()));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + dto.getVehicleId()));

        if (!vehicle.getOwner().getId().equals(user.getId())) {
            throw new InvalidDataException("The selected vehicle does not belong to the specified customer.");
        }

        Visit visit = visitMapper.toEntity(dto);
        visit.setUser(user);
        visit.setVehicle(vehicle);
        visit.setStatus(VisitStatus.PENDING);

        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            List<com.portfolio.smartgarage.model.Service> services = serviceRepository.findAllById(dto.getServiceIds());

            if (services.size() != dto.getServiceIds().size()) {
                throw new ResourceNotFoundException("One or more service IDs are invalid.");
            }

            visit.setServices(services);

            BigDecimal total = services.stream()
                    .map(com.portfolio.smartgarage.model.Service::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            visit.setTotalPrice(total);
        } else {
            visit.setTotalPrice(BigDecimal.ZERO);
        }

        Visit savedVisit = visitRepository.save(visit);
        return visitReportMapper.toViewDto(savedVisit);
    }

    @Override
    @Transactional
    public VisitViewDto registerVisitForNewCustomer(NewCustomerVisitDto dto) {
        // basic uniqueness checks - prefer repository lookups to avoid DB constraint exceptions
        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new ResourceAlreadyExistsException("A user with the provided email already exists.");
        });
        userRepository.findByPhoneNumber(dto.getPhoneNumber()).ifPresent(u -> {
            throw new ResourceAlreadyExistsException("A user with the provided phone number already exists.");
        });

        String rawPassword = PasswordGenerator.generate();

        User newUser = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .phoneNumber(dto.getPhoneNumber())
                .role(Role.CUSTOMER)
                .build();
        User savedUser = userRepository.save(newUser);

        // create vehicle from DTO
        VehicleRequestDto vehicleReq = new VehicleRequestDto(
                dto.getLicensePlate(), dto.getVin(), dto.getYear(), dto.getModel(), dto.getBrand()
        );

        // Optional: check for existing VIN or license plate
        vehicleRepository.findByLicensePlate(dto.getLicensePlate()).ifPresent(v -> {
            throw new ResourceAlreadyExistsException("Vehicle with the same license plate already exists.");
        });
        vehicleRepository.findByVin(dto.getVin()).ifPresent(v -> {
            throw new ResourceAlreadyExistsException("Vehicle with the same VIN already exists.");
        });

        Vehicle vehicle = vehicleMapper.toEntity(vehicleReq);
        vehicle.setOwner(savedUser);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // create visit entity
        Visit visit = new Visit();
        visit.setDate(dto.getVisitDate());
        visit.setAdditionalComments(dto.getAdditionalComments());
        visit.setUser(savedUser);
        visit.setVehicle(savedVehicle);
        visit.setStatus(VisitStatus.PENDING);

        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            List<com.portfolio.smartgarage.model.Service> services = serviceRepository.findAllById(dto.getServiceIds());
            if (services.size() != dto.getServiceIds().size()) {
                throw new ResourceNotFoundException("One or more service IDs are invalid.");
            }
            visit.setServices(services);
            BigDecimal total = services.stream()
                    .map(com.portfolio.smartgarage.model.Service::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            visit.setTotalPrice(total);
        } else {
            visit.setTotalPrice(BigDecimal.ZERO);
        }

        Visit savedVisit = visitRepository.save(visit);

        VisitViewDto response = visitReportMapper.toViewDto(savedVisit);
        // include temporary password in a dedicated, non-persistent field so callers can relay it to the customer
        response.setAdditionalComments((response.getAdditionalComments() == null ? "" : response.getAdditionalComments() + " ")
                + "TEMP_PASS: " + rawPassword);

        return response;
    }

    @Override
    public List<VisitViewDto> getVisitsByUser(Long userId, Long vehicleId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found.");
        }

        List<Visit> visits = (vehicleId != null)
                ? visitRepository.findAllByUserIdAndVehicleId(userId, vehicleId)
                : visitRepository.findAllByUserId(userId);

        return visits.stream()
                .map(visitReportMapper::toViewDto)
                .collect(Collectors.toList());
    }

    @Override
    public VisitViewDto getVisitDetails(Long visitId, String currency) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit record not found."));

        // If currency provided, convert, otherwise return base
        return (currency == null || currency.isBlank())
                ? visitReportMapper.toViewDto(visit)
                : visitReportMapper.toViewDtoWithCurrency(visit, currency);
    }


    @Override
    public VisitViewDto getVisitDetailsForCustomer(Long visitId, Long userId, String currency) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit record not found."));

        if (!visit.getUser().getId().equals(userId)) {
            throw new InvalidDataException("Access denied. This visit record does not belong to you.");
        }

        return (currency == null || currency.isBlank())
                ? visitReportMapper.toViewDto(visit)
                : visitReportMapper.toViewDtoWithCurrency(visit, currency);
    }

    @Override
    public long getVisitCountByDate(LocalDate date) {
        return visitRepository.countByDate(date);
    }

    @Override
    public Map<LocalDate, String> getAvailabilityPlan(LocalDate startDate) {
        Map<LocalDate, String> calendar = new LinkedHashMap<>();

        for (int i = 0; i < 14; i++) {
            LocalDate date = startDate.plusDays(i);
            long count = visitRepository.countByDate(date);

            String status = (count >= 6)
                    ? "FULL - No slots available"
                    : "AVAILABLE - " + (6 - count) + " slots left";

            calendar.put(date, status);
        }
        return calendar;
    }

    @Override
    @Transactional
    public void deleteVisit(Long visitId) {
        if (!visitRepository.existsById(visitId)) {
            throw new ResourceNotFoundException("Visit not found.");
        }
        visitRepository.deleteById(visitId);
    }
}