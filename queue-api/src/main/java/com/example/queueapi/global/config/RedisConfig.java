package com.example.queueapi.global.config;

import com.example.queueapi.global.config.properties.RedisClusterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Profile("local")
@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    private final RedisClusterProperties redisClusterProperties;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(new RedisClusterConfiguration()
                .clusterNode(redisClusterProperties.host1(), redisClusterProperties.port1())
                .clusterNode(redisClusterProperties.host2(), redisClusterProperties.port2())
                .clusterNode(redisClusterProperties.host3(), redisClusterProperties.port3())
                .clusterNode(redisClusterProperties.host4(), redisClusterProperties.port4())
                .clusterNode(redisClusterProperties.host5(), redisClusterProperties.port5())
                .clusterNode(redisClusterProperties.host6(), redisClusterProperties.port6())
        );
    }
}
