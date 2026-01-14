package com.portfolio.smartgarage.repository.specifications;

import com.portfolio.smartgarage.model.*;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ClientVehicleSpecifications {

    public static Specification<ClientVehicle> hasLicensePlate(String lp) {
        return (root, query, cb) -> lp == null || lp.isEmpty() ? null :
                cb.like(cb.lower(root.get("licensePlate")), "%" + lp.toLowerCase() + "%");
    }

    public static Specification<ClientVehicle> hasVin(String vin) {
        return (root, query, cb) -> vin == null || vin.isEmpty() ? null :
                cb.equal(root.get("vin"), vin);
    }

    public static Specification<ClientVehicle> hasOwnerName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) return null;
            Join<ClientVehicle, User> ownerJoin = root.join("owner");
            return cb.or(
                    cb.like(cb.lower(ownerJoin.get("firstName")), "%" + name.toLowerCase() + "%"),
                    cb.like(cb.lower(ownerJoin.get("lastName")), "%" + name.toLowerCase() + "%")
            );
        };
    }

    public static Specification<ClientVehicle> hasOwnerEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isEmpty()) return null;
            Join<ClientVehicle, User> ownerJoin = root.join("owner");
            return cb.like(cb.lower(ownerJoin.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<ClientVehicle> hasBrand(String brand) {
        return (root, query, cb) -> {
            if (brand == null || brand.isEmpty()) return null;
            Join<ClientVehicle, Vehicle> vehicleJoin = root.join("vehicle");
            Join<Vehicle, Model> modelJoin = vehicleJoin.join("model");
            Join<Model, Brand> brandJoin = modelJoin.join("brand");
            return cb.like(cb.lower(brandJoin.get("name")), "%" + brand.toLowerCase() + "%");
        };
    }

    public static Specification<ClientVehicle> hasOwnerPhone(String phone) {
        return (root, query, cb) -> {
            if (phone == null || phone.isEmpty()) return null;
            Join<ClientVehicle, User> ownerJoin = root.join("owner");
            return cb.like(ownerJoin.get("phoneNumber"), "%" + phone + "%");
        };
    }
}