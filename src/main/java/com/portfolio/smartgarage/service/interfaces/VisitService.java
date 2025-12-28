package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;

import java.util.List;

public interface VisitService {

    VisitViewDto createVisit(CreateVisitDto dto);

    List<VisitViewDto> getVisitsByUser(Long userId);

    List<VisitViewDto> getVisitsByVehicle(Long vehicleId);

    void deleteVisit(Long visitId);
}
