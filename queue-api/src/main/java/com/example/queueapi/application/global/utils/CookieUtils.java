package com.example.queueapi.application.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

public class CookieUtils {

    public static String getCookie(HttpServletRequest request, String target) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(it -> it.getName().equalsIgnoreCase(target))
                    .findFirst()
                    .orElse(new Cookie(target, ""))
                    .getValue();
        }

        return "";
    }
}
