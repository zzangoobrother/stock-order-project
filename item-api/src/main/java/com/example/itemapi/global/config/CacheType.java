package com.example.itemapi.global.config;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum CacheType {
    ITEM_INFO("itemInfo", Duration.ofSeconds(24 * 60 * 60), 10000)
    ;

    private final String cacheName;
    private final Duration expiredAfterWrite;
    private final int maximumSize;

    CacheType(String cacheName, Duration expiredAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize;
    }
}
