package com.example.orderapi.global.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FeignCookieInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new IllegalArgumentException("토큰을 입력해주세요.");
        }

        String cookieStr = Arrays.stream(cookies)
                .map(it -> it.getName() + "=" + it.getValue())
                .collect(Collectors.joining("; "));

        requestTemplate.header("Cookie", cookieStr);
    }
}
