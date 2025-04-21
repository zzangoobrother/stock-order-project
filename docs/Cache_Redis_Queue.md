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
@Cacheable(cacheManager = "localCacheManager", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId", sync = true)
public Item getBy(Long itemId) {
    return itemRepository.findByIdAndIsDeleteFalse(itemId).orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
}
```

- 캐시 키 : 'itemInfo:{itemId}'
- TTL : 1시간
- 조건 : itemId가 null이 아닐 경우면 캐시 적용
- 동기화 : 'sync = true'로 설정하여 동시 요청 시 중복 연산 방지

### 3.2 캐시 적용의 합리성

#### 빈번한 조회 데이터 캐싱 :
- 사용 가능한 제품 조회는 자주 조회되는 데이터 이다.
- 이러한 데이터를 캐싱함으로써 반복적인 데이터베이스 쿼리를 줄여 전체적인 시스템 성능을 향상시킬 수 있다.

#### TTL 설정 :
- 각 데이터의 특성과 변경 빈도를 고려한 TTL 고려, 데이터의 최신성과 시스템 성능 사이의 최적의 균형을 찾기 위한 전략이다.

#### 조건부 캐싱 :
- itemId의 유효성을 검사하여 불필요한 캐싱을 방지하도록 한다.
- 무의미한 데이터가 캐시를 차지하는 것을 방지하고, 캐시 공간을 효율적으로 사용할 수 있는 것을 기대했다.

#### 동기화 설정 :
- 'sync = true' 설정으로 동시에 여러 요청이 들어올 경우, 한 번만 데이터베이스에 접근하여 캐시를 생성하도록 했다.
- 이는 동시성 문제를 해결하고, 불필요한 데이터베이스 쿼리를 방지하여 시스템의 안정성과 효율성을 높이는 것을 기대했다.

<br>

## 4) 캐시 수정 (Put) 분석
### 4.1 decreaseStock 분석

```java
@CachePut(cacheManager = "localCacheManager", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId"),
public Item decreaseStock(Long itemId, int decreaseCount) {
    Item item = itemRepository.findByIdAndIsDeleteFalse(itemId).orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
    item.decreaseStock(decreaseCount);

    return item;
}
```

- 제품 정보 캐시를 수정한다.
- 제품 재고가 변경될 때 호출되어 최신성을 보장하도록 한다.
- 실제 데이터 변경과 캐시 수정이 동시에 이루어져 데이터 일관성을 보장할 수 있다.

### 4.2 캐시 수정 합리성
- 데이터 일관성 유지 :
  - 재고 차감이 발생하여 재고가 변경될 때 관련 캐시를 수정한다.
  - 이는 캐시된 데이터와 실제 데이터베이스의 데이터 간 불일치를 방지하여 사용자에게 항상 정확한 정보를 제공한다.

- 선별적 캐시 수정 :
  - 전체 캐시가 아닌 변경된 제품 정보만 캐시 수정을 한다.
  - 제품 정보 (1시간 TTL) 캐시를 각각 무효화한다.
  - 불필요한 캐시를 방지하고, 시스템 리소스를 효율적으로 사용하게 한다.

- 실시간성 보장 :
  - 중요한 상태 변경 발생 시 즉시 캐시를 수정하도록 한다.
  - 이를 통해 사용자에게 항상 최신 정보를 제공할 수 있으며, 실시간 가용성 정보의 정확성을 보장한다.

<br>


