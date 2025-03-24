package com.example.itemapi.global.config;

import com.example.itemapi.global.config.properties.RedisClusterProperties;
import com.example.itemapi.global.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

    @Bean
    RedisTemplate<String, Object> objectRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        ObjectMapper mapper = ObjectMapperUtils.objectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));

        return template;
    }
}
