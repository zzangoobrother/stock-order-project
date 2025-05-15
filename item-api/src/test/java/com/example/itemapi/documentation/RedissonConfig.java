package com.example.itemapi.documentation;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class RedissonConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Value("${spring.data.cluster.redis.host1}")
    String host1;
    @Value("${spring.data.cluster.redis.port1}")
    int port1;
    @Value("${spring.data.cluster.redis.host2}")
    String host2;
    @Value("${spring.data.cluster.redis.port2}")
    int port2;
    @Value("${spring.data.cluster.redis.host3}")
    String host3;
    @Value("${spring.data.cluster.redis.port3}")
    int port3;
    @Value("${spring.data.cluster.redis.host4}")
    String host4;
    @Value("${spring.data.cluster.redis.port4}")
    int port4;
    @Value("${spring.data.cluster.redis.host5}")
    String host5;
    @Value("${spring.data.cluster.redis.port5}")
    int port5;
    @Value("${spring.data.cluster.redis.host6}")
    String host6;
    @Value("${spring.data.cluster.redis.port6}")
    int port6;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress(
                        REDISSON_HOST_PREFIX + host1 + ":" + port1,
                        REDISSON_HOST_PREFIX + host2 + ":" + port2,
                        REDISSON_HOST_PREFIX + host3 + ":" + port3,
                        REDISSON_HOST_PREFIX + host4 + ":" + port4,
                        REDISSON_HOST_PREFIX + host5 + ":" + port5,
                        REDISSON_HOST_PREFIX + host6 + ":" + port6
                )
                .setScanInterval(2000);

        return Redisson.create(config);
    }
}
