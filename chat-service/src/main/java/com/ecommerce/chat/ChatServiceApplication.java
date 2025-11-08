package com.ecommerce.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({
		"ecommerce.chat",
		"ecommerce.library"
})
public class ChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatServiceApplication.class, args);
	}

}
