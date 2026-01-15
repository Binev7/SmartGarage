package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitAdminReportDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.model.VisitStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VisitService {

    VisitViewDto createVisit(CreateVisitDto dto);

    List<VisitViewDto> getVisitsByUser(Long userId, Long vehicleId);

    VisitViewDto getVisitDetails(Long visitId, String currency);

    VisitAdminReportDto getAdminReport(Long visitId);

    VisitViewDto registerVisitForNewCustomer(NewCustomerVisitDto dto);

    long getVisitCountByDate(LocalDate date);

    Map<LocalDate, String> getCalendarAvailability(LocalDate startDate);

    void deleteVisit(Long visitId);

    VisitViewDto updateStatus(Long visitId, VisitStatus newStatus);

}
