package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.dto.invoice.InvoiceDto;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.InvoiceMapper;
import com.portfolio.smartgarage.model.ServiceOrder;
import com.portfolio.smartgarage.repository.ServiceOrderRepository;
import com.portfolio.smartgarage.service.interfaces.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private final ServiceOrderRepository orderRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceData(Long orderId) {
        ServiceOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Order not found: " + orderId));

        return invoiceMapper.toInvoiceDto(order);
    }
}