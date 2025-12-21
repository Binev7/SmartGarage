package com.portfolio.smartgarage.repository;

import com.portfolio.smartgarage.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findAllByVehicleId(Long vehicleId);

    List<Visit> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    long countByDate(LocalDate date);
}
