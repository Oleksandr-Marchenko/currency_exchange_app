package com.dev.currencyexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyExchangeAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeAppApplication.class, args);
	}

}
