package com.example.queueapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class QueueApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueueApiApplication.class, args);
    }

}
