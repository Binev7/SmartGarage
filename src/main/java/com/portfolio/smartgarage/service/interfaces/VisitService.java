package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VisitService {

    VisitViewDto createVisit(CreateVisitDto dto);

    public List<VisitViewDto> getVisitsByUser(Long userId, Long vehicleId);

    public VisitViewDto getVisitDetails(Long visitId, String currency);

    VisitViewDto registerVisitForNewCustomer(NewCustomerVisitDto dto);

    long getVisitCountByDate(LocalDate date);

    Map<LocalDate, String> getCalendarAvailability(LocalDate startDate);

    void deleteVisit(Long visitId);
}
