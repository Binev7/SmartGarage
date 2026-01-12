package com.portfolio.smartgarage.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('EMPLOYEE') or @securityService.isVehicleOwner(#id)")
public @interface IsVehicleOwnerOrEmployee {
}