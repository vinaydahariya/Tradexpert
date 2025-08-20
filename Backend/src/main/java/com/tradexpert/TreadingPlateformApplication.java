package com.tradexpert;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TreadingPlateformApplication {

    public static void main(String[] args) {
        // Load .env file from project root
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // optional, agar .env missing ho to ignore kare
                .load();

        // Set each variable as system property
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(TreadingPlateformApplication.class, args);
    }
}
