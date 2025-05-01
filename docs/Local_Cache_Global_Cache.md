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

## 5) 캐시 무효화 (Eviction) 분석
### 5.1 ItemManager 분석

```java
@CacheEvict(cacheManager = "localCacheManager", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId")
public void deleteBy(Long itemId) {
    Item item = itemRepository.findByIdAndIsDeleteFalse(itemId)
            .orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
    item.delete();
}
```

'deleteBy()' 메서드는 캐시 무효화를 담당한다.

- 제품 정보 캐시를 무효화한다.
- 'itemInfo:#itemId' 키를 가진 캐시 항목을 제거한다.
- 제품이 삭제될 때 호출되어 최신성을 보장하도록 한다.
- 실제 데이터 변경과 캐시 무효화가 동시에 이루어져 데이터 일관성을 보장할 수 있다.

### 5.2 캐시 무효화의 합리성
- 데이터 일관성 유지 :
  - 제품 삭제로 상태가 변경될 때 관련 캐시를 즉시 무효화한다.
  - 이는 캐시된 데이터와 실제 데이터베이스의 데이터 간 불일치를 방지하여 사용자에게 항상 정확한 정보를 제공한다.

- 선별적 캐시 무효화 :
  - 전체 캐시가 아닌 변경된 제품 정보만 캐시만 무효화한다.
  - 제품 정보 (1시간 TTL) 캐시를 각각 무효화한다.
  - 불필요한 캐시를 방지하고, 시스템 리소스를 효율적으로 사용하게 한다.

- 실시간성 보장 :
  - 중요한 상태 변경 발생시 즉시 캐시를 무효화하도록 한다.
  - 이를 통해 사용자에게 항상 최신 정보를 제공할 수 있으며, 실시간 가용성 정보의 정확성을 보장한다.

<br>

## 6) 성능 개선 효과 분석
### 6.1 쿼리 최적화

- 중복 쿼리 요청 감소 : 캐시를 통해 동일한 데이터에 대한 반복적인 데이터베이스 쿼리를 크게 줄일 수 있다.
- 복잡한 쿼리 결과 캐싱 : 복잡한 쿼리 결과를 캐싱함으로써, 데이터베이스의 부하를 크게 감소시킬 수 있다.
- 쿼리의 복잡도 :
  - 데이터 모델의 복잡도로 생기는 많은 JOIN
  - 복잡한 WHERE 조건절
  - GROUP BY와 HAVING 절

#### 캐시 사용으로 인한 개선
- 복잡한 쿼리는 캐시 적용 후 캐시 히트 시 실행되지 않아, 데이터베이스 부하를 크게 줄일 수 있다.
- 특히 대량의 동시 접속이 예상되는 시스템에서, 이러한 캐싱 전략은 데이터베이스의 부하를 크게 줄이고 전체 시스템의 응답 시간을 개선하는 데 중요한 역할을 한다고 생각한다.

### 6.2 대량 트래픽 처리 능력 향상

- 응답 시간 단축 :
  - 캐시된 데이터를 사용함으로써, 데이터베이스 조회 시간을 크게 줄여 전체적인 응답 시간을 단축시킬 수 있다.
  - ex) 데이터베이스 쿼리 발생시 100ms가 소요되던 요청이 캐시 히트 시 10ms 이내로 단축될 수 있다.

- 데이터베이스 부하 분산 :
  - 캐시를 통해 데이터베이스의 직접적인 요청을 줄임으로써, 대량의 트래픽 발생 시에도 데이터베이스의 부하를 효과적으로 분산시킬 수 있다.
  - ex) 초당 1000건 요청 중 80%가 캐시에서 처리된다면, 데이터베이스는 초당 200건의 요청만 처리하면 된다.

### 5.3 실시간 데이터 제공

- TTL을 통한 데이터 정합성 유지 :
  - TTL 설정을 통해, 캐시의 이점을 누리면서도 적절한 주기로 데이터를 갱신하여 실시간성을 보장한다.
  - ex) 빠른 갱신으로 실시간 상황을 반영하고, 상대적으로 안정적인 데이터를 효율적으로 제공한다.

- 상태 변경 시 즉시 캐시 무효화 :
  - 중요한 상태 변경 시 즉시 캐시를 무효화하여, 사용자에게 항상 최신 정보를 제공한다.
  - ex) 제품 제고가 매진되면 즉시 캐시를 갱신한다.

<br>

## 7) 글로벌 Cache 적용
### 7.1 글로벌 Cache 설정 코드 분석

```java
@RequiredArgsConstructor
@Configuration
@EnableCaching
public class CacheConfig {
  @Bean("redisCacheManger")
  public CacheManager redisCacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
    PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(Object.class)
            .build();

    ObjectMapper mapper = ObjectMapperUtils.objectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule())
            .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
            .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(mapper)));

    return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(lettuceConnectionFactory)
            .cacheDefaults(redisCacheConfiguration)
            .build();
  }
}
```

- `RedisCacheManager` Bean을 정의하여 Redis 캐시 매니저를 커스터마이즈한다.
- `serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))` : 캐시 키를 문자열로 직렬화한다.
- `serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(mapper))` : 캐시 값을 JSON 형식으로 직렬화한다.

## 8) 최종 Cache 처리 방법
### 7.1 Cache 계층화

1. 아키텍쳐 설계
   - 서비스 레이어 (ItemService) : 전체적인 재고 처리 로직 조정
   - 캐시 계층 레이어 (ItemManager) : 로컬 Cache, 글로벌 Cache 적용

2. Cache 계층화
   - 로컬 Cache : Spring cache 처리
   - Redis를 이용한 글로벌 Cache : Redis cache 처리

```java
@Slf4j
@RequiredArgsConstructor
@Service
public class ItemService {

  private final ItemManager itemManager;
  
  public ItemServiceDto.ItemInfo getBy(Long itemId) {
    Item item = itemManager.getBy(itemId);
    return ItemServiceDto.ItemInfo.toItemInfo(item);
  }
}

@Transactional(readOnly = true)
@Caching(cacheable = {
        @Cacheable(cacheManager = "localCacheManager", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId", sync = true),
        @Cacheable(cacheManager = "redisCacheManger", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId", sync = true)
})
public Item getBy(Long itemId) {
  return itemRepository.findByIdAndIsDeleteFalse(itemId).orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
}
```

### 구현 결정 사항 및 이유
1. Cache를 통한 성능 개선 :
   - 2 계층 적용 (로컬 cache + 글로벌 cache) :
   - 로컬 cache로 1차 cache 조회를 수행한다.
   - 글로벌 cache로 2차 조회를 마련하여 성능 개선을 한다.

## 최종 결론

- 데이터베이스 부하 감소 : 반복적인 쿼리를 cache로 대체하여 데이터베이스 부하를 크게 줄인다.
- 응답 시간 단축 : 복잡한 쿼리 결과를 cache에서 즉시 제공하여 응답 시간을 대폭 단축시킨다.
- 데이터 일관성 유지 : 적절한 TTL 설정과 상태 변경 시 즉시 cache 무효화를 통해 데이터의 일관성을 유지한다.
- 실시간성 보장 : 중요 데이터의 빠른 갱신과 즉시 cache 무효화를 통해 실시간 정보를 제공한다.
