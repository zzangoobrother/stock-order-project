package com.example.itemapi.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class LocalCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
                .map(it -> new CaffeineCache(it.getCacheName(), Caffeine.newBuilder().recordStats()
                        .expireAfterWrite(it.getExpiredAfterWrite(), TimeUnit.SECONDS)
                        .maximumSize(it.getMaximumSize())
                        .build()))
                .toList();

        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
