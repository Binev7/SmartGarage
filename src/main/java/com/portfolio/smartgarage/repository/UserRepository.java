package com.portfolio.smartgarage.repository;

import com.portfolio.smartgarage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT v.owner FROM Vehicle v WHERE v.licensePlate = :plate")
    Optional<User> findByVehicleLicensePlate(String plate);

    @Query("SELECT v.owner FROM Vehicle v WHERE v.vin = :vin")
    Optional<User> findByVehicleVin(String vin);
}