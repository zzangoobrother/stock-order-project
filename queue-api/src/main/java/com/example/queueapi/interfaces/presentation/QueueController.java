package com.example.queueapi.interfaces.presentation;

import com.example.queueapi.application.global.utils.CookieUtils;
import com.example.queueapi.application.service.QueueService;
import com.example.queueapi.application.service.dto.QueueServiceDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
@RestController
public class QueueController {
    private final String QUEUE_WAIT_TOKEN = "queue-wait-%s-token";

    private final QueueService queueService;

    @GetMapping
    public ResponseEntity<Long> registerUser(@RequestParam String queue, @RequestParam Long userId) {
        QueueServiceDto queueServiceDto = queueService.registerUser(queue, userId);

        ResponseCookie cookie = ResponseCookie.from(QUEUE_WAIT_TOKEN.formatted(queue), queueServiceDto.token())
                .maxAge(Duration.ofSeconds(5))
                .path("/")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.COOKIE, cookie.toString())
                .body(queueServiceDto.rank());
    }

    @GetMapping("/rank")
    public long getRankUser(@RequestParam String queue, HttpServletRequest request) {
        String token = CookieUtils.getCookie(request, QUEUE_WAIT_TOKEN.formatted(queue));
        return queueService.getRankUser(queue, token);
    }
}
