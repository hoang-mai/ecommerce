package com.ecommerce.chat.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
		"com.ecommerce.chat.notification",
		"com.ecommerce.library"
})
public class ChatNotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatNotificationServiceApplication.class, args);
	}

}
