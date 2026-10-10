package com.infy;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class TaxTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaxTrackerApplication.class, args);
	}
	@Bean
	ModelMapper getModelMapper() {
		return new ModelMapper();
	}
}
