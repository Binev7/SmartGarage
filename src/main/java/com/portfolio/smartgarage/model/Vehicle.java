package com.portfolio.smartgarage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1,2}[0-9]{4}[A-Z]{2}$", message = "Wrong license plate format")
    @Column(unique = true, nullable = false)
    private String licensePlate;

    @NotBlank
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    @Column(unique = true, nullable = false)
    private String vin;

    @Min(value = 1887, message = "Year must be no earlier than 1887")
    private int year;

    @NotBlank
    @Size(min = 2, max = 50)
    private String model;

    @NotBlank
    @Size(min = 2, max = 50)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
}