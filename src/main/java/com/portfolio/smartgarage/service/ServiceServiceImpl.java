package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.ServiceRequestDto;
import com.portfolio.smartgarage.dto.ServiceResponseDto;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.mapper.ServiceMapper;
import com.portfolio.smartgarage.model.Service;
import com.portfolio.smartgarage.repository.ServiceRepository;
import com.portfolio.smartgarage.repository.VisitRepository;
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
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteService(Long serviceId) {
        Service s = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service with id " + serviceId + " not found"));

        visitRepository.deleteAllByServiceId(serviceId);

        serviceRepository.delete(s);
    }
}
