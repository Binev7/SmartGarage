package com.portfolio.smartgarage.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
public @interface IsEmployeeOrCustomer {
}