package com.example.queueapi.application.service;

import com.example.queueapi.application.service.dto.QueueServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueueService {
    private final String USER_QUEUE_WAIT_KEY = "user:queue:%s:wait";
    private final String USER_QUEUE_TOKEN = "user:queue:%s:%d";
    private final String USER_QUEUE_PROCEED_KEY = "user:queue:%s:proceed";

    private final StringRedisTemplate stringRedisTemplate;

    public QueueServiceDto registerUser(String queue, Long userId) {
        String token = createToken(queue, userId);
        long unixTimestamp = Instant.now().getEpochSecond();
        Boolean flag = stringRedisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY.formatted(queue), token, unixTimestamp);

        if (!flag) {
            throw new IllegalArgumentException("동일한 token이 존재합니다.");
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

    public long getRankUser(String queue, String token) {
        Long rank = stringRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), token);
        if (rank == null) {
            return -1;
        }

        return rank >= 0 ? rank + 1 : rank;
    }

    public boolean isAllowedUser(String queue, String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Long rank = stringRedisTemplate.opsForZSet().rank(USER_QUEUE_PROCEED_KEY.formatted(queue), token);
        if (rank == null) {
            rank = -1L;
        }

        return rank >= 0;
    }

    /**
     *  진입 허용
     *  1. wait queue 사용자 제거
     *  2. proceed queue 사용자 추가
     */
    public Long allowUser(String queue, int count) {
        AtomicInteger result = new AtomicInteger();
        stringRedisTemplate.opsForZSet().popMin(USER_QUEUE_WAIT_KEY.formatted(queue), count).forEach(it -> {
            Boolean saveFlag = stringRedisTemplate.opsForZSet().add(USER_QUEUE_PROCEED_KEY.formatted(queue), it.getValue(), Instant.now().getEpochSecond());
            if (saveFlag) {
                result.getAndIncrement();
            }
        });

        return result.longValue();
    }
}
