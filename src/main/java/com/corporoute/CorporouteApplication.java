package com.corporoute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CorporouteApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorporouteApplication.class, args);
	}

}
