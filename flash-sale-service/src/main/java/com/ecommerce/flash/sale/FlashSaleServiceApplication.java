package com.ecommerce.flash.sale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@ComponentScan({
		"com.ecommerce.flash.sale",
		"com.ecommerce.library"
})
public class FlashSaleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlashSaleServiceApplication.class, args);
	}

}
