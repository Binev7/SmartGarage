package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.service.ServiceRequestDto;
import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.mapper.ServiceMapper;
import com.portfolio.smartgarage.model.Service;
import com.portfolio.smartgarage.repository.ServiceRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final VisitRepository visitRepository;

    @Override
    @Transactional
    public ServiceResponseDto createService(ServiceRequestDto dto) {
        if (serviceRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ResourceAlreadyExistsException("Service with name " + dto.getName() + " already exists");
        }

        Service entity = serviceMapper.toEntity(dto);
        Service saved = serviceRepository.save(entity);
        return serviceMapper.toDto(saved);
    }

    @Override
    public ServiceResponseDto getServiceById(Long serviceId) {
        Service s = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service with id " + serviceId + " not found"));
        return serviceMapper.toDto(s);
    }

    @Override
    public List<ServiceResponseDto> getAllServices() {
        List<ServiceResponseDto> list = serviceRepository.findAll().stream()
                .map(serviceMapper::toDto)
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No services found");
        }

        return list;
    }

    @Override
    @Transactional
    public void deleteService(Long serviceId) {
        Service s = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service with id " + serviceId + " not found"));

        // delete visit-service join rows first to avoid FK constraint issues
        visitRepository.deleteAllByServiceId(serviceId);

        serviceRepository.delete(s);
    }
}
