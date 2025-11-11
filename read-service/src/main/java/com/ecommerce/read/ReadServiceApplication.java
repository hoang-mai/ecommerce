package com.ecommerce.read;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({
        "com.ecommerce.read",
        "com.ecommerce.library"
})
public class ReadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadServiceApplication.class, args);
	}

}
