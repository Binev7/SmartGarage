package com.portfolio.smartgarage.repository.specifications;

import com.portfolio.smartgarage.model.Brand;
import com.portfolio.smartgarage.model.Model;
import com.portfolio.smartgarage.model.Vehicle;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecifications {

    public static Specification<Vehicle> hasBrand(String brand) {
        return (root, query, cb) -> {
            if (brand == null || brand.isEmpty()) return null;
            Join<Vehicle, Model> modelJoin = root.join("model");
            Join<Model, Brand> brandJoin = modelJoin.join("brand");
            return cb.like(cb.lower(brandJoin.get("name")), "%" + brand.toLowerCase() + "%");
        };
    }

    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null || model.isEmpty()) return null;
            Join<Vehicle, Model> modelJoin = root.join("model");
            return cb.like(cb.lower(modelJoin.get("name")), "%" + model.toLowerCase() + "%");
        };
    }

    public static Specification<Vehicle> hasYear(Integer year) {
        return (root, query, cb) -> year == null ? null : cb.equal(root.get("year"), year);
    }
}