package com.example.queueapi.application.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtils {

    public static Optional<String> getCookie(HttpServletRequest request, String target) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(target))
                .map(Cookie::getValue)
                .findFirst();
    }
}
