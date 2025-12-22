package com.ecommerce.saga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.ecommerce.saga",
        "com.ecommerce.library"
})
public class SagaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaServiceApplication.class, args);
    }

}
