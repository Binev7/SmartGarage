package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.validator.VisitValidator;
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
    private final ClientVehicleRepository clientVehicleRepository;
    private final VehicleRepository vehicleRepository; // За проверка в каталога
    private final ServiceRepository serviceRepository;
    private final CurrencyService currencyService;
    private final VisitValidator visitValidator;
    private final VisitMapper visitMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public VisitViewDto createVisit(CreateVisitDto dto) {
        visitValidator.validateDailyLimit(dto.getDate().toLocalDate(), MAX_DAILY_VISITS);

        // 1. Намираме реалната кола (ClientVehicle)
        ClientVehicle clientVehicle = clientVehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Client Vehicle not found with ID: " + dto.getVehicleId()));

        User owner = clientVehicle.getOwner();

        // 2. Валидираме, че колата наистина принадлежи на потребителя (ако е нужно)
        visitValidator.validateVehicleOwnership(owner, clientVehicle);

        Visit visit = visitMapper.toEntity(dto);
        visit.setUser(owner);
        visit.setClientVehicle(clientVehicle); // Сочи към ClientVehicle
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

        // 1. Създаваме новия потребител
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

        // 2. Намираме шаблона от каталога (BMW/Audi и т.н.)
        // ВАЖНО: Тук NewCustomerVisitDto трябва да съдържа catalogVehicleId
        Vehicle catalogVehicle = vehicleRepository.findById(dto.getCatalogVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Catalog model not found."));

        // 3. Създаваме запис за конкретната кола на клиента
        ClientVehicle clientVehicle = ClientVehicle.builder()
                .licensePlate(dto.getLicensePlate())
                .vin(dto.getVin())
                .vehicle(catalogVehicle)
                .owner(savedUser)
                .build();
        ClientVehicle savedVehicle = clientVehicleRepository.save(clientVehicle);

        // 4. Създаваме самото посещение
        Visit visit = visitMapper.toEntity(dto);
        visit.setUser(savedUser);
        visit.setClientVehicle(savedVehicle);
        visit.setStatus(VisitStatus.PENDING);

        applyServicesAndCalculateTotal(visit, dto.getServiceIds());

        Visit savedVisit = visitRepository.save(visit);
        VisitViewDto response = visitMapper.toDto(savedVisit);

        // Добавяме временната парола в коментарите (за тестване)
        String passComment = "TEMP_PASS: " + rawPassword;
        response.setAdditionalComments(response.getAdditionalComments() == null
                ? passComment
                : response.getAdditionalComments() + " " + passComment);

        return response;
    }

    @Override
    public List<VisitViewDto> getVisitsByUser(Long userId, Long clientVehicleId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found.");
        }

        // Използваме обновения метод от VisitRepository
        List<Visit> visits = (clientVehicleId != null)
                ? visitRepository.findAllByUserIdAndClientVehicleId(userId, clientVehicleId)
                : visitRepository.findAllByUserId(userId);

        return visits.stream()
                .map(visitMapper::toDto)
                .collect(Collectors.toList());
    }

    // Останалите методи остават до голяма степен същите, но работят с обновения Visit модел
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
            String status = (count >= MAX_DAILY_VISITS) ? "FULL" : "AVAILABLE (" + (MAX_DAILY_VISITS - count) + ")";
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