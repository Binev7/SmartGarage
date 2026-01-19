package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.dto.visit.NewCustomerVisitDto;
import com.portfolio.smartgarage.dto.visit.VisitAdminReportDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.constant.BaseConstants;
import com.portfolio.smartgarage.helper.util.CreateAndSaveHelper;
import com.portfolio.smartgarage.helper.util.VisitHelper;
import com.portfolio.smartgarage.helper.validator.UserValidator;
import com.portfolio.smartgarage.helper.validator.VisitValidator;
import com.portfolio.smartgarage.helper.mapper.VisitMapper;
import com.portfolio.smartgarage.model.*;
import com.portfolio.smartgarage.repository.*;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import com.portfolio.smartgarage.helper.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {


    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final ClientVehicleRepository clientVehicleRepository;
    private final VisitValidator visitValidator;
    private final VisitMapper visitMapper;
    private final CreateAndSaveHelper createAndSaveHelper;
    private final UserValidator userValidator;
    private final VisitHelper visitHelper;

    @Override
    @Transactional
    public VisitViewDto createVisit(CreateVisitDto dto) {
        visitValidator.validateDailyLimit(
                dto.getDate().toLocalDate(),
                BaseConstants.MAX_DAILY_VISITS
        );

        ClientVehicle vehicle = clientVehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        visitValidator.validateVehicleOwnership(vehicle.getOwner(), vehicle);

        Visit savedVisit = createAndSaveHelper.createAndSaveVisit(dto, vehicle);

        return visitHelper.mapWithCurrency(savedVisit, null);
    }

    @Override
    public Map<LocalDate, String> getCalendarAvailability(LocalDate startDate) {
        return visitHelper.generateAvailabilityCalendar(
                startDate,
                BaseConstants.CALENDAR_DAYS_HORIZON,
                BaseConstants.MAX_DAILY_VISITS
        );
    }

    @Override
    @Transactional
    public VisitViewDto registerVisitForNewCustomer(NewCustomerVisitDto dto) {
        userValidator.validateRegistration(dto);

        String rawPassword = PasswordGenerator.generate();
        User savedUser = createAndSaveHelper.createAndSaveUser(dto, rawPassword);
        ClientVehicle savedVehicle = createAndSaveHelper.createAndSaveVehicle(dto, savedUser);

        Visit savedVisit = createAndSaveHelper.createAndSaveVisit(dto, savedUser, savedVehicle);

        VisitViewDto response = visitMapper.toDto(savedVisit);
        visitHelper.appendPasswordToComments(response, rawPassword);

        return response;
    }

    @Override
    public List<VisitViewDto> getVisitsByUser(Long userId, Long clientVehicleId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found.");
        }

        List<Visit> visits = (clientVehicleId != null)
                ? visitRepository.findAllByUserIdAndClientVehicleId(userId, clientVehicleId)
                : visitRepository.findAllByUserId(userId);

        return visits.stream()
                .map(visitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VisitViewDto getVisitDetails(Long visitId, String currency) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit record not found."));

        // Ползваме стандартния toDto за клиенти
        VisitViewDto dto = visitMapper.toDto(visit);
        return visitHelper.applyCurrency(dto, visit.getTotalPrice(), currency);
    }

    @Override
    public VisitAdminReportDto getAdminReport(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit record not found."));

        VisitAdminReportDto adminDto = visitMapper.toAdminReportDto(visit);

        return visitHelper.applyCurrency(adminDto, visit.getTotalPrice(), BaseConstants.BASE_CURRENCY);
    }

    @Override
    public long getVisitCountByDate(LocalDate date) {
        return visitRepository.countByDate(date);
    }

    @Override
    @Transactional
    public void deleteVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found."));

        if (VisitStatus.COMPLETED.equals(visit.getStatus())) {
            throw new IllegalStateException("Cannot delete a completed visit record as it contains historical financial and service data.");
        }

        visitRepository.delete(visit);
    }

    @Override
    @Transactional
    public VisitViewDto updateStatus(Long visitId, VisitStatus newStatus) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        visit.setStatus(newStatus);
        return visitMapper.toDto(visitRepository.save(visit));
    }
}