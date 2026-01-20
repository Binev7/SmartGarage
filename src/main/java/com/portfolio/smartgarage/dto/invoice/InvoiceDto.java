package com.portfolio.smartgarage.dto.invoice;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class InvoiceDto {
    private String invoiceNumber;
    private String clientName;
    private String vehicleModel;
    private String licensePlate;
    private List<ServiceItemDto> items;
    private Double totalAmount;
    private String currencyCode;
}


