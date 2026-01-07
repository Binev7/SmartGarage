package com.portfolio.smartgarage.dto.visit;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewCustomerVisitDto {

    @NotBlank private String username;
    @Email @NotBlank private String email;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String phoneNumber;

    @NotBlank private String brand;
    @NotBlank private String model;
    private int year;
    @NotBlank private String licensePlate;
    @NotBlank private String vin;

    private List<Long> serviceIds;
    @NotNull
    private LocalDateTime visitDate;
    private String additionalComments;
}