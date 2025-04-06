package com.example.queueapi.application.service;

import com.example.queueapi.application.service.dto.QueueServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueueService {
    private final String USER_QUEUE_WAIT_KEY = "user:queue:%s:wait";
    private final String USER_QUEUE_TOKEN = "user-queue-%s-%d";

    private final StringRedisTemplate stringRedisTemplate;

    public QueueServiceDto registerUser(String queue, Long userId) {
        String token = createToken(queue, userId);
        long unixTimestamp = Instant.now().getEpochSecond();
        Boolean add = stringRedisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY.formatted(queue), token, unixTimestamp);

        if (!add) {
            throw new IllegalArgumentException("");
        }

        Long rank = stringRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), token);
        return new QueueServiceDto(token, rank >= 0 ? rank + 1 : rank);
    }

    private String createToken(String queue, Long userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = USER_QUEUE_TOKEN.formatted(queue, userId);
            byte[] encodeHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hash : encodeHash) {
                hexString.append(String.format("%02x", hash));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("queue token create error");
        }

        return "";
    }
}
