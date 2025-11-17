package com.codesungrape.hmcts.bookapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Book API Spring Boot application. Starts the Spring ApplicationContext and
 * embedded server.
 */
@SpringBootApplication
public class BookApiApplication {

    /**
     * Private constructor to prevent instantiation.
     * This avoids Checkstyle treating this as an instantiable utility class.
     */
    private BookApiApplication() {
        // Intentionally empty — prevents accidental instantiation
    }

    /**
     * Main entry point — starts the Spring Boot application.
     */
    public static void main(String[] args) {

        SpringApplication.run(BookApiApplication.class, args);

    }
}
