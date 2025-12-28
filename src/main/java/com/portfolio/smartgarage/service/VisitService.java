package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.CreateVisitDto;
import com.portfolio.smartgarage.dto.VisitViewDto;

import java.util.List;

public interface VisitService {

    VisitViewDto createVisit(CreateVisitDto dto);

    List<VisitViewDto> getVisitsByUser(Long userId);

    List<VisitViewDto> getVisitsByVehicle(Long vehicleId);

    void deleteVisit(Long visitId);
}
