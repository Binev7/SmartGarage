package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.CreateVisitDto;
import com.portfolio.smartgarage.dto.VisitViewDto;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.exception.InvalidDataException;
import com.portfolio.smartgarage.mapper.VisitMapper;
import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.model.Visit;
import com.portfolio.smartgarage.model.VisitStatus;
import com.portfolio.smartgarage.repository.UserRepository;
import com.portfolio.smartgarage.repository.VehicleRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
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

        Visit savedVisit = visitRepository.save(visit);
        return visitMapper.toDto(savedVisit);
    }

    @Override
    public List<VisitViewDto> getVisitsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }

        return visitRepository.findAllByUserId(userId)
                .stream()
                .map(visitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitViewDto> getVisitsByVehicle(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle with id " + vehicleId + " not found");
        }

        return visitRepository.findAllByVehicleId(vehicleId)
                .stream()
                .map(visitMapper::toDto)
                .collect(Collectors.toList());
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
