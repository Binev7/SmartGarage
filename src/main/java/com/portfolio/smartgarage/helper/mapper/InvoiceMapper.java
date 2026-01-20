package com.portfolio.smartgarage.helper.mapper;

import com.portfolio.smartgarage.dto.invoice.InvoiceDto;
import com.portfolio.smartgarage.dto.invoice.ServiceItemDto;
import com.portfolio.smartgarage.helper.util.DisplayFormater;
import com.portfolio.smartgarage.model.Service;
import com.portfolio.smartgarage.model.ServiceOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {

    private final DisplayFormater displayFormater;

    public InvoiceDto toInvoiceDto(ServiceOrder order) {
        if (order == null) return null;

        var cv = order.getClientVehicle();

        return InvoiceDto.builder()
                .invoiceNumber("INV-" + order.getId())
                .clientName(cv.getOwner().getFirstName() + " " + cv.getOwner().getLastName())
                .vehicleModel(displayFormater.formatFullName(cv))
                .licensePlate(cv.getLicensePlate())
                .items(mapServiceItems(order.getServices()))
                .totalAmount(displayFormater.toDouble(order.getTotalAmount()))
                .currencyCode("BGN")
                .build();
    }

    public ServiceItemDto toServiceItemDto(Service service) {
        if (service == null) return null;

        return ServiceItemDto.builder()
                .serviceName(service.getName())
                .price(displayFormater.toDouble(service.getPrice()))
                .build();
    }

    private List<ServiceItemDto> mapServiceItems(List<Service> services) {
        if (services == null) return Collections.emptyList();
        return services.stream()
                .map(this::toServiceItemDto)
                .collect(Collectors.toList());
    }
}