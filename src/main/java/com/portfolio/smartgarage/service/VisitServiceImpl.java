package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.vehicle.VehicleRequestDto;
import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.exception.InvalidDataException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.validator.VisitValidator;
import com.portfolio.smartgarage.helper.mapper.VehicleMapper;
import com.portfolio.smartgarage.helper.mapper.VisitMapper;
import com.portfolio.smartgarage.model.*;
import com.portfolio.smartgarage.repository.*;
import com.portfolio.smartgarage.service.interfaces.CurrencyService;
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

    private static final int MAX_DAILY_VISITS = 6;
    private static final int CALENDAR_DAYS_HORIZON = 14;
    private static final String BASE_CURRENCY = "BGN";

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final CurrencyService currencyService;
    private final VisitValidator visitValidator;
    private final VisitMapper visitMapper;
    private final VehicleMapper vehicleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public VisitViewDto createVisit(CreateVisitDto dto) {

        visitValidator.validateDailyLimit(dto.getDate().toLocalDate(), MAX_DAILY_VISITS);

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + dto.getVehicleId()));

        User owner = vehicle.getOwner();

        Visit visit = visitMapper.toEntity(dto);
        visit.setUser(owner);
        visit.setVehicle(vehicle);
        visit.setStatus(VisitStatus.PENDING);

        applyServicesAndCalculateTotal(visit, dto.getServiceIds());

        Visit savedVisit = visitRepository.save(visit);
        return visitMapper.toDto(savedVisit);
    }

    @Override
    @Transactional
    public VisitViewDto registerVisitForNewCustomer(NewCustomerVisitDto dto) {
        visitValidator.validateDailyLimit(dto.getDate().toLocalDate(), MAX_DAILY_VISITS);
        visitValidator.validateNewCustomerUniqueness(dto);

        String rawPassword = PasswordGenerator.generate();
        User newUser = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .phoneNumber(dto.getPhoneNumber())
                .role(Role.CUSTOMER)
                .build();
        User savedUser = userRepository.save(newUser);

        VehicleRequestDto vehicleReq = new VehicleRequestDto(
                dto.getLicensePlate(), dto.getVin(), dto.getYear(), dto.getModel(), dto.getBrand()
        );
        Vehicle vehicle = vehicleMapper.toEntity(vehicleReq);
        vehicle.setOwner(savedUser);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        Visit visit = visitMapper.toEntity(dto);
        visit.setUser(savedUser);
        visit.setVehicle(savedVehicle);
        visit.setStatus(VisitStatus.PENDING);

        applyServicesAndCalculateTotal(visit, dto.getServiceIds());

        Visit savedVisit = visitRepository.save(visit);

        VisitViewDto response = visitMapper.toDto(savedVisit);

        String passComment = "TEMP_PASS: " + rawPassword;
        response.setAdditionalComments(response.getAdditionalComments() == null
                ? passComment
                : response.getAdditionalComments() + " " + passComment);

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
                .map(visitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VisitViewDto getVisitDetails(Long visitId, String currency) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit record not found."));

        return convertAndMapVisit(visit, currency);
    }

    @Override
    public long getVisitCountByDate(LocalDate date) {
        return visitRepository.countByDate(date);
    }

    @Override
    public Map<LocalDate, String> getCalendarAvailability(LocalDate startDate) {
        Map<LocalDate, String> calendar = new LinkedHashMap<>();

        for (int i = 0; i < CALENDAR_DAYS_HORIZON; i++) {
            LocalDate date = startDate.plusDays(i);
            long count = visitRepository.countByDate(date);

            String status = (count >= MAX_DAILY_VISITS)
                    ? "FULL - No slots available"
                    : "AVAILABLE - " + (MAX_DAILY_VISITS - count) + " slots left";

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

    private void applyServicesAndCalculateTotal(Visit visit, List<Long> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            visit.setTotalPrice(BigDecimal.ZERO);
            return;
        }

        List<com.portfolio.smartgarage.model.Service> services = serviceRepository.findAllById(serviceIds);
        if (services.size() != serviceIds.size()) {
            throw new ResourceNotFoundException("One or more service IDs are invalid.");
        }

        visit.setServices(services);
        BigDecimal total = services.stream()
                .map(com.portfolio.smartgarage.model.Service::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        visit.setTotalPrice(total);
    }

    private VisitViewDto convertAndMapVisit(Visit visit, String targetCurrency) {
        VisitViewDto dto = visitMapper.toDto(visit);

        if (targetCurrency != null && !targetCurrency.isBlank() && !targetCurrency.equalsIgnoreCase(BASE_CURRENCY)) {
            BigDecimal convertedTotal = currencyService.convert(visit.getTotalPrice(), BASE_CURRENCY, targetCurrency);
            dto.setTotalPrice(convertedTotal);
            dto.setCurrency(targetCurrency.toUpperCase());
        } else {
            dto.setCurrency(BASE_CURRENCY);
        }

        return dto;
    }
}