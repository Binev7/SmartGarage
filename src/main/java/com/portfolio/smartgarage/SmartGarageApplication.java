package com.portfolio.smartgarage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class SmartGarageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartGarageApplication.class, args);
	}

}
