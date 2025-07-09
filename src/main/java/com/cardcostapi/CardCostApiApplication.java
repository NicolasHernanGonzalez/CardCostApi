package com.cardcostapi;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;


@SpringBootApplication(scanBasePackages = "com.cardcostapi")
@EnableCaching
public class CardCostApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardCostApiApplication.class, args);
	}

}
