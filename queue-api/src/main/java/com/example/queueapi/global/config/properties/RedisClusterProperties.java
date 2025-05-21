package com.example.queueapi.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.cluster.redis")
public record RedisClusterProperties(
        String host1,
        int port1,
        String host2,
        int port2,
        String host3,
        int port3,
        String host4,
        int port4,
        String host5,
        int port5,
        String host6,
        int port6
) {
}
