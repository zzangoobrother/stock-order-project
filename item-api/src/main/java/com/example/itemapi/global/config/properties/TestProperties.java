package com.example.itemapi.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config")
public record TestProperties(
        String test
) {
}
