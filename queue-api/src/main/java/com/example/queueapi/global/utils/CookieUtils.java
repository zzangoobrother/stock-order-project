package com.example.queueapi.global.utils;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtils {

    public static Optional<String> getCookie(Cookie[] cookies, String target) {
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(target))
                .map(Cookie::getValue)
                .findFirst();
    }
}
