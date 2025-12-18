package com.ecommerce.read;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.ecommerce.read",
        "com.ecommerce.library"
})
public class ReadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadServiceApplication.class, args);
	}

}
