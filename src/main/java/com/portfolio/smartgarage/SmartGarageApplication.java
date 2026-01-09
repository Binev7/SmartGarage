package com.portfolio.smartgarage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmartGarageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartGarageApplication.class, args);
	}

}
