package com.portfolio.smartgarage.security.service;

import com.portfolio.smartgarage.repository.ClientVehicleRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final ClientVehicleRepository clientVehicleRepository;
    private final VisitRepository visitRepository;

    public boolean isVehicleOwner(Long vehicleId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return clientVehicleRepository.findById(vehicleId)
                .map(v -> v.getOwner().getUsername().equals(currentUsername))
                .orElse(false);
    }

    public boolean isVisitOwner(Long visitId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return visitRepository.findById(visitId)
                .map(v -> v.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }
}