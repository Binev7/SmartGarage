package com.portfolio.smartgarage.service;

import com.portfolio.smartgarage.dto.ServiceRequestDto;
import com.portfolio.smartgarage.dto.ServiceResponseDto;

import java.util.List;

public interface ServiceService {

    ServiceResponseDto createService(ServiceRequestDto dto);

    ServiceResponseDto getServiceById(Long serviceId);

    List<ServiceResponseDto> getAllServices();

    void deleteService(Long serviceId);
}

