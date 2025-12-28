package com.portfolio.smartgarage.repository;

import com.portfolio.smartgarage.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByNameContainingIgnoreCase(String name);

    List<Service> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    boolean existsByNameIgnoreCase(String name);
}
