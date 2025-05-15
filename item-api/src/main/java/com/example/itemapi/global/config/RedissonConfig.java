package com.example.itemapi.global.config;

import com.example.itemapi.global.config.properties.RedisClusterProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@RequiredArgsConstructor
@Configuration
public class RedissonConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";

    private final RedisClusterProperties redisClusterProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress(
                        REDISSON_HOST_PREFIX + redisClusterProperties.host1() + ":" + redisClusterProperties.port1(),
                        REDISSON_HOST_PREFIX + redisClusterProperties.host2() + ":" + redisClusterProperties.port2(),
                        REDISSON_HOST_PREFIX + redisClusterProperties.host3() + ":" + redisClusterProperties.port3(),
                        REDISSON_HOST_PREFIX + redisClusterProperties.host4() + ":" + redisClusterProperties.port4(),
                        REDISSON_HOST_PREFIX + redisClusterProperties.host5() + ":" + redisClusterProperties.port5(),
                        REDISSON_HOST_PREFIX + redisClusterProperties.host6() + ":" + redisClusterProperties.port6()
                )
                .setScanInterval(2000);

        return Redisson.create(config);
    }
}
