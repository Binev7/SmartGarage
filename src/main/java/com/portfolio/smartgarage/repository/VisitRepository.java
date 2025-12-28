package com.portfolio.smartgarage.repository;

import com.portfolio.smartgarage.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findAllByUserId(Long userId);

    List<Visit> findAllByVehicleId(Long vehicleId);

    boolean existsByVehicleId(Long vehicleId);

    @Modifying
    @Transactional
    @Query("delete from Visit v where v.vehicle.id = :vehicleId")
    void deleteAllByVehicleId(@Param("vehicleId") Long vehicleId);
}
