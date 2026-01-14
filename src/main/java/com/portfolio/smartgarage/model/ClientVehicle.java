package com.portfolio.smartgarage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_vehicles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String vin;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();
}