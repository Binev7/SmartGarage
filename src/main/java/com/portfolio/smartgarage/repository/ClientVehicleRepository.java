package com.portfolio.smartgarage.repository;

import com.portfolio.smartgarage.model.ClientVehicle;
import com.portfolio.smartgarage.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository

public interface ClientVehicleRepository extends JpaRepository<ClientVehicle, Long>, JpaSpecificationExecutor<ClientVehicle> {

    Optional<ClientVehicle> findByLicensePlate(String licensePlate);

    Optional<ClientVehicle> findByVin(String vin);

    List<ClientVehicle> findAllByOwnerId(Long ownerId);
}