package com.portfolio.smartgarage.repository;

import com.portfolio.smartgarage.model.Visit;
import com.portfolio.smartgarage.model.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findAllByUserId(Long userId);

    List<Visit> findAllByUserIdAndClientVehicleId(Long userId, Long clientVehicleId);

    boolean existsByClientVehicleIdAndStatusIn(Long vehicleId, List<VisitStatus> statuses);

    @Modifying
    @Transactional
    @Query("delete from Visit v where v.clientVehicle.id = :clientVehicleId")
    void deleteAllByClientVehicleId(@Param("clientVehicleId") Long clientVehicleId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM visit_services WHERE service_id = :serviceId", nativeQuery = true)
    void deleteAllByServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT COUNT(v) FROM Visit v WHERE CAST(v.date AS date) = :date")
    long countByDate(@Param("date") LocalDate date);
}
