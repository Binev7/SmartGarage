package com.portfolio.smartgarage.helper.util;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.VisitMapper;
import com.portfolio.smartgarage.model.*;
import com.portfolio.smartgarage.repository.*;
import com.portfolio.smartgarage.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateAndSaveHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VehicleRepository vehicleRepository;
    private final ClientVehicleRepository clientVehicleRepository;
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final ServiceRepository serviceRepository;
    private final EmailService emailService;


    public Visit createAndSaveVisit(CreateVisitDto dto, ClientVehicle vehicle) {
        Visit visit = visitMapper.toEntity(dto);
        visit.setUser(vehicle.getOwner());
        visit.setClientVehicle(vehicle);
        visit.setStatus(VisitStatus.PENDING);

        applyServicesAndCalculateTotal(visit, dto.getServiceIds());
        return visitRepository.save(visit);
    }

    public User createAndSaveUser(NewCustomerVisitDto dto, String rawPassword) {
        User newUser = User.builder()
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .phoneNumber(dto.getPhoneNumber())
                .role(Role.CUSTOMER)
                .build();

        User savedUser = userRepository.save(newUser);

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // Имейлът ще се изпрати само ако всичко в базата е записано успешно
                    emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName(), rawPassword);
                }
            });
        }

        return savedUser;
    }

    public ClientVehicle createAndSaveVehicle(NewCustomerVisitDto dto, User owner) {
        Vehicle catalogVehicle = vehicleRepository.findById(dto.getCatalogVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Catalog model not found."));

        ClientVehicle clientVehicle = ClientVehicle.builder()
                .licensePlate(dto.getLicensePlate())
                .vin(dto.getVin())
                .vehicle(catalogVehicle)
                .owner(owner)
                .registeredAt(LocalDateTime.now())
                .build();
        return clientVehicleRepository.save(clientVehicle);
    }

    public Visit createAndSaveVisit(NewCustomerVisitDto dto, User user, ClientVehicle vehicle) {
        Visit visit = visitMapper.toEntity(dto);
        visit.setUser(user);
        visit.setClientVehicle(vehicle);
        visit.setStatus(VisitStatus.PENDING);

        applyServicesAndCalculateTotal(visit, dto.getServiceIds());
        return visitRepository.save(visit);
    }

    public void applyServicesAndCalculateTotal(Visit visit, List<Long> serviceIds) {
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

}
