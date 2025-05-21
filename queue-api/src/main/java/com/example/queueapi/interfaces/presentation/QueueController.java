package com.example.queueapi.interfaces.presentation;

import com.example.queueapi.global.utils.CookieUtils;
import com.example.queueapi.application.service.QueueService;
import com.example.queueapi.application.service.dto.QueueServiceDto;
import jakarta.servlet.http.Cookie;
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
import java.util.Optional;

@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
@RestController
public class QueueController {
    private final String QUEUE_WAIT_TOKEN = "queue-wait-%s-token";

    private final QueueService queueService;

    // 대기열 등록 및 wait token 생성
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

    // 현재 대기 번호 조회
    @GetMapping("/rank")
    public long getRankUser(@RequestParam String queue, HttpServletRequest request) {
        String token = getToken(QUEUE_WAIT_TOKEN.formatted(queue), request.getCookies());
        return queueService.getRankUser(queue, token);
    }

    // 대기열 허락 여부 조회
    @GetMapping("/allowed")
    public boolean isAllowedUser(@RequestParam String queue, HttpServletRequest request) {
        String token = getToken(QUEUE_WAIT_TOKEN.formatted(queue), request.getCookies());
        return queueService.isAllowedUser(queue, token);
    }

    private String getToken(String target, Cookie[] cookies) {
        Optional<String> cookie = CookieUtils.getCookie(cookies, target);
        if (!cookie.isPresent()) {
            throw new IllegalArgumentException("해당 토큰을 찾을 수 없습니다.");
        }

        return cookie.get();
    }
}
