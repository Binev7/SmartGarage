package com.portfolio.smartgarage.repository;

import com.portfolio.smartgarage.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    List<Model> findAllByBrandIdAndActiveTrue(Long brandId);

    Optional<Model> findByNameAndBrandId(String name, Long brandId);

    @Modifying
    @Transactional
    @Query("UPDATE Model m SET m.active = false WHERE m.brand.id = :brandId")
    void deactivateAllByBrandId(@Param("brandId") Long brandId);
}