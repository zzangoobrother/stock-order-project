package com.example.queueapi.documentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Profile("test")
@Configuration
public class RedisConfig {

    @Value("${spring.data.cluster.redis.host1}")
    String host1;
    @Value("${spring.data.cluster.redis.port1}")
    int port1;

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(new RedisClusterConfiguration()
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
        );
    }
}
