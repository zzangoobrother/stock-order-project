package com.example.itemapi.global.config;

import lombok.Getter;

@Getter
public enum CacheType {
    ITEM_INFO("itemInfo", 24 * 60 * 60, 10000)
    ;

    private String cacheName;
    private int expiredAfterWrite;
    private int maximumSize;

    CacheType(String cacheName, int expiredAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize;
    }
}
