package com.antares.gsc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GscApp {
    public static void main(String[] args) {
        SpringApplication.run(GscApp.class, args);
    }
}
