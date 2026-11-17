package com.alejandro.meetingbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MeetingbookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetingbookingApplication.class, args);
	}

}
