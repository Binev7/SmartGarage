package com.portfolio.smartgarage.repository;

import com.portfolio.smartgarage.model.Model;
import com.portfolio.smartgarage.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    List<Vehicle> findAllByModelIdAndActiveTrue(Long modelId);

    boolean existsByModelIdAndYear(Long modelId, int year);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.active = false WHERE v.model.id = :modelId")
    void deactivateAllByModelId(@Param("modelId") Long modelId);

    List<Vehicle> findAllByActiveTrue();

    Optional<Vehicle> findByModelIdAndYearAndActiveTrue(Long modelId, Integer year);

    Optional<Vehicle> findByModelIdAndYear(Long modelId, Integer year);
    
    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.active = false WHERE v.model.brand.id = :brandId")
    void deactivateAllByBrandId(@Param("brandId") Long brandId);
}