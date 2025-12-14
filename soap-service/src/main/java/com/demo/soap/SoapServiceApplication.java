package com.demo.soap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SOAP Service Application
 * This service receives SOAP requests for order processing.
 * Runs on port 8081.
 */
@SpringBootApplication
public class SoapServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoapServiceApplication.class, args);
    }
}
