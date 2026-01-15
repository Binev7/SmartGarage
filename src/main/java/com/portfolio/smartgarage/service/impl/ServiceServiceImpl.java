package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.dto.service.ServiceRequestDto;
import com.portfolio.smartgarage.dto.service.ServiceResponseDto;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.ServiceMapper;
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
    public List<ServiceResponseDto> searchServicesByName(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name).stream()
                .map(serviceMapper::toDto)
                .collect(Collectors.toList());
    }

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
    @Transactional
    public ServiceResponseDto updateService(Long id, ServiceRequestDto dto) {
        Service existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service with id " + id + " not found"));

        if (!existing.getName().equalsIgnoreCase(dto.getName()) &&
            serviceRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ResourceAlreadyExistsException("Service with name " + dto.getName() + " already exists");
        }
        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        Service updated = serviceRepository.save(existing);
        return serviceMapper.toDto(updated);
    }


    @Override
    public ServiceResponseDto getServiceById(Long serviceId) {
        Service s = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service with id " + serviceId + " not found"));
        return serviceMapper.toDto(s);
    }

    @Override
    public List<ServiceResponseDto> getAllServices() {
        List<ServiceResponseDto> list = serviceRepository.findAllByOrderByNameAsc().stream()
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

        visitRepository.deleteAllByServiceId(serviceId);

        serviceRepository.delete(s);
    }
}
