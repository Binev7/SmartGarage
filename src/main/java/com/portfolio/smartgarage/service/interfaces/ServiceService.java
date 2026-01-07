package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.service.ServiceRequestDto;
import com.portfolio.smartgarage.dto.service.ServiceResponseDto;

import java.util.List;

public interface ServiceService {

    ServiceResponseDto createService(ServiceRequestDto dto);

    ServiceResponseDto updateService(Long id, ServiceRequestDto dto);

    ServiceResponseDto getServiceById(Long serviceId);

    List<ServiceResponseDto> getAllServices();

    void deleteService(Long serviceId);
}
