package com.example.itemapi.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisPublisher {
    private final RedisTemplate<String, Object> objectRedisTemplate;

    public void publish(String channel, Long id) {
        objectRedisTemplate.convertAndSend(channel, id);
    }
}
