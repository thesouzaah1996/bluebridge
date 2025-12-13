package com.blue.bridge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.blue.bridge.notification.dto.NotificationDTO;
import com.blue.bridge.notification.service.NotificationService;
import com.blue.bridge.users.entity.User;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class BridgeApplication {

	private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(BridgeApplication.class, args);
	}

	// @Bean
	// CommandLineRunner runner() {
	// 	return args -> {
	// 		NotificationDTO notificationDTO = NotificationDTO.builder()
	// 			.recipient("aratechsistemas@gmail.com")
	// 			.subject("Testing email")
	// 			.message("Test Worked")
	// 			.build();
	// 		notificationService.sendEmail(notificationDTO, new User());
	// 	};
	// }

}
