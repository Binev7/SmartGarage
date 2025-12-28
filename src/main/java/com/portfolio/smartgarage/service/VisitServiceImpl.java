package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.exception.InvalidDataException;
import com.portfolio.smartgarage.mapper.VisitMapper;
import com.portfolio.smartgarage.model.Service;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.model.Visit;
import com.portfolio.smartgarage.model.VisitStatus;
import com.portfolio.smartgarage.repository.ServiceRepository;
import com.portfolio.smartgarage.repository.UserRepository;
import com.portfolio.smartgarage.repository.VehicleRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final VisitMapper visitMapper;

    @Override
    @Transactional
    public VisitViewDto createVisit(CreateVisitDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + dto.getUserId() + " not found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + dto.getVehicleId() + " not found"));

        Visit visit = visitMapper.toEntity(dto);
        visit.setUser(user);
        visit.setVehicle(vehicle);
        visit.setStatus(VisitStatus.PENDING);

        // Attach services if provided
        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            List<Service> services = serviceRepository.findAllById(dto.getServiceIds());
            if (services.size() != dto.getServiceIds().size()) {
                // Some service IDs not found
                List<Long> foundIds = services.stream().map(Service::getId).collect(Collectors.toList());
                List<Long> missing = dto.getServiceIds().stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
                throw new ResourceNotFoundException("Services not found for ids: " + missing);
            }
            visit.setServices(services);

            // compute total price
            BigDecimal total = services.stream()
                    .map(Service::getPrice)
                    .filter(p -> p != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            visit.setTotalPrice(total);
        }

        Visit savedVisit = visitRepository.save(visit);
        return visitMapper.toDto(savedVisit);
    }

    @Override
    public List<VisitViewDto> getVisitsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }

        List<VisitViewDto> result = visitRepository.findAllByUserId(userId)
                .stream()
                .map(visitMapper::toDto)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No visits found for user with id " + userId);
        }

        return result;
    }

    @Override
    public List<VisitViewDto> getVisitsByVehicle(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle with id " + vehicleId + " not found");
        }

        List<VisitViewDto> result = visitRepository.findAllByVehicleId(vehicleId)
                .stream()
                .map(visitMapper::toDto)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No visits found for vehicle with id " + vehicleId);
        }

        return result;
    }

    @Override
    @Transactional
    public void deleteVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit with id " + visitId + " not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new InvalidDataException("Unauthorized");
        }

        String principal = auth.getName();
        User currentUser = userRepository.findByEmail(principal)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        boolean isOwner = visit.getUser().getId().equals(currentUser.getId());
        boolean isEmployee = currentUser.getRole() != null && currentUser.getRole().name().equals("EMPLOYEE");

        if (!isOwner && !isEmployee) {
            throw new InvalidDataException("You are not allowed to delete this visit");
        }

        visitRepository.delete(visit);
    }
}
