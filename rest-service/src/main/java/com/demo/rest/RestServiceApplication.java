package com.demo.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * REST Service Application
 * This service receives REST requests, maps them to SOAP requests using MapStruct,
 * calls the SOAP service, and maps the response back to REST format.
 * Runs on port 8081.
 */
@SpringBootApplication
public class RestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }
}
