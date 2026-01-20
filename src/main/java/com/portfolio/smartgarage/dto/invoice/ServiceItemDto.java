package com.portfolio.smartgarage.dto.invoice;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceItemDto {
    private String serviceName;
    private Double price;
}