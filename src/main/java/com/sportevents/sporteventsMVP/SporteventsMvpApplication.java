package com.sportevents.sporteventsMVP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SporteventsMvpApplication {

	public static void main(String[] args) {

		SpringApplication.run(SporteventsMvpApplication.class, args);
	}

}

