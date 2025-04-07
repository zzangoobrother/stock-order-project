package com.example.queueapi.interfaces.job;

import com.example.queueapi.application.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class QueueJob {
    private final String USER_QUEUE_WAIT_KEY_FOR_SCAN = "user:queue:*:wait";

    private final QueueService queueService;
    private final StringRedisTemplate stringRedisTemplate;

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void allowUser() {
        int maxAllowUserCount = 1;

        Optional<String> optionalQueue = stringRedisTemplate.scan(ScanOptions.scanOptions()
                        .match(USER_QUEUE_WAIT_KEY_FOR_SCAN)
                        .count(100)
                        .build())
                .stream()
                .map(it -> it.split(":")[2])
                .findFirst();
        if (!optionalQueue.isPresent()) {
            log.info("empty queue wait");
            return;
        }

        String queue = optionalQueue.get();
        Long result = queueService.allowUser(queue, maxAllowUserCount);

        log.info("queue 허용 개수 : {}", result);
    }
}
