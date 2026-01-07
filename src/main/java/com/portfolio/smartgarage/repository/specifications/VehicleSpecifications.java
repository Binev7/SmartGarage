package com.portfolio.smartgarage.repository.specifications;

import com.portfolio.smartgarage.model.User;
import com.portfolio.smartgarage.model.Vehicle;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecifications {

    public static Specification<Vehicle> hasBrand(String brand) {
        return (root, query, cb) -> brand == null || brand.isEmpty() ? null :
                cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
    }

    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) -> model == null || model.isEmpty() ? null :
                cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%");
    }

    public static Specification<Vehicle> hasYear(Integer year) {
        return (root, query, cb) -> year == null ? null :
                cb.equal(root.get("year"), year);
    }

    public static Specification<Vehicle> hasLicensePlate(String lp) {
        return (root, query, cb) -> lp == null || lp.isEmpty() ? null :
                cb.equal(root.get("licensePlate"), lp);
    }

    public static Specification<Vehicle> hasOwnerName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) return null;
            // Правим join с таблицата на собственика
            Join<Vehicle, User> ownerJoin = root.join("owner");
            return cb.like(cb.lower(ownerJoin.get("firstName")), "%" + name.toLowerCase() + "%");
        };
    }
}
