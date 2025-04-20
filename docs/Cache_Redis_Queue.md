# Cache 사용과 대기열 시스템을 위한 Redis 사용

# 1. Cache 도입을 통한 성능 개선

## 1) 개요
- 제품 조회에 Cache를 도입한 내용을 분석한다.
- 주요 목적은 Cache를 도입한 이유와 분석, 그리고 도입한 과정과 결과를 설명한다.
- 대규모 트래픽이 예상되는 이커머스 재고 관리 및 주문하기 시스템에서 Cache 도입은 데이터베이스 부하 감소와 응답 시간 단축을 위한 핵심 전략이다.

<br>

## 2) Cache 설정 분석

### 2.1 로컬 Cache 설정 코드 분석

```java
@RequiredArgsConstructor
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean("localCacheManager")
    public CacheManager simpleCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(createdCaffeineCache());
        return cacheManager;
    }

    @Bean
    public List<CaffeineCache> createdCaffeineCache() {
        return Arrays.stream(CacheType.values())
                .map(it -> new CaffeineCache(it.getCacheName(), Caffeine.newBuilder().recordStats()
                        .expireAfterWrite(it.getExpiredAfterWrite())
                        .maximumSize(it.getMaximumSize())
                        .build()))
                .toList();
    }
}

@Getter
public enum CacheType {
    ITEM_INFO("itemInfo", Duration.ofSeconds(1 * 60 * 60), 10000)
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
```

- `@EnableCaching` 어노테이션을 사용하여 Sprinng의 캐싱 기능을 활성화 한다. 이는 Spring Framework에 캐시 관련 기능을 자동으로 구성하도록 지시한다.
- `SimpleCacheManager` Bean을 정의하여 로컬 Cache를 커스터마이즈한다. 그리고 로컬 Cache 라이브러리 중 Caffeine Cache를 사용한다.
  - 이를 통해 서로 다른 TTL을 가진 Cache 설정을 구성한다.

<br>

### 2.2 캐시 설정의 합리성

#### 다양한 TTL 설정
  - 데이터 특성에 따라 TTL을 설정함으로써, 데이터의 정합성과 캐시의 효율성을 균형있게 조절할 수 있다.
  - 차별화된 TTL 설정은 데이터의 특성을 고려한 최적화된 캐시 전략을 구현한 것으로, 캐시이ㅢ 효율성을 높이면서도 데이터의 정합성을 적절히 유지할 수 있다.

이러한 캐시 설정은 대규모 트래픽이 예상되는 시스템에서 데이터베이스 부하를 줄이고 응답 시간을 단축시키는 데 크게 기여할 것으로 예상된다.  
특히 데이터의 특성에 따라 차별화된 TTL을 적용하여 시스템의 전반적인 성능 향상을 도모할 수 있다.

<br>

## 3) Cache 적용 분석

### 3.1 ItemManager의 캐시 적용

#### 3.1.1 getBy() 메서드

```java

```
