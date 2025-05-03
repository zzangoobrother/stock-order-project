# 많은 수의 인원을 수용할 수 았는 대기열 시스템을 위한 Redis에 관하여

## 1) 개요
- 대기열 시스템을 Redis를 활용한 시스템으로 과정을 상세히 기술한다.
- 대규모 인원을 수용할 수 있는 효율적인 대기열 시스템 구축을 목표로 진행되었다.

<br>

## 2) Redis 기반 시스템으로 구성
### 2.1 Redis 선택 이유

- 고성능 :
  - 메모리 기반 데이터 구조로 빠른 읽기/쓰기 가능
  - Redis는 초당 100,000개 이상의 읽기/쓰기 작업을 처리할 수 있으며, 대규모 동시 접속 상황에서도 빠른 응답 시간을 유지할 수 있다.

- 확장성 :
  - 대규모 동시 접속 처리에 적합
  - AWS의 ElastiCache 등 Redis Cluster를 사용하여 수평적 확장이 가능하며, 필요에 따라 노드를 추가하여 처리 용량을 늘릴 수 있다.

- 실시간 처리 :
  - 대기열 상태 변경 및 조회를 즉시 반영 가능
  - Redis의 `ZADD', `ZRANK` 명령어를 사용하여 대기열 추가 및 위치 조회를 ms 단위의 지연으로 처리할 수 있다.

- 데이터 구조의 다양성 :
  - Sorted Set 등을 활용한 효율적인 대기열 관리 가능
  - Sorted Set을 사용하여 대기열을 구현하면, `O(log(N))` 시간 복잡도로 대기열 위치를 조회할 수 있다.

<br>

### 2.2 주요 구성 요소
- Sorted Set 사용
  - Sorted Set의 데이터 구조 중 하나로, 각 요소가 score와 연관되어 있는 정렬된 집합이다.
  - 이를 통해 대기열의 순서를 효율적으로 관리할 수 있다.

- 대기열 키 : user:queue:%s:wait, 처리 중인 대기열 키 : user:queue:%s:proceed
  - user:queue:%s:wait 키는 대기 중인 사용자들의 정보를 저장하도록 한다.
  - user:queue:%s:proceed 키는 현재 처리 중인 사용자들의 정보를 저장한다.
  - 각 키는 Sorted Set으로 구현되어 있어 효율적인 순서 관리가 가능하다.

### 2.3 구현 과정 및 고려사항
#### 2.3.1 Redis 구성

- Redis 설정 클래스 (RedisConfig) 구현 :
  - 이 클래스는 Redis 연결 및 데이터 직렬화/역직렬화 방식을 정의한다.
  - StringRedisTemplate은 Redis에서 기본적으로 String 타입의 Key, Value 쉽게 작업할 수 있게 제공해준다.
  - 대기열 시스템에서 주로 문자열 기반의 토큰과 사용자 ID를 다루기 때문에 선택했다.

#### 2.3.2 데이터 구조 설계
Redis의 Sorted Set을 사용한 대기열 구현은 다음과 같은 이점을 제공한다.

- 효율적인 순서 관리 : Sorted Set은 O(log N) 시간 복잡도로 요소 추가, 제거, 순위 조회가 가능한다.
- 시간 기반 정렬 : 시스템 현재 시간을 score로 사용함으로써, FIFO 방식의 대기열을 자연스럽게 구현할 수 있다. 즉 선착순으로 구현이 가능하다.
- 범위 쿼리 효율성 : 특정 시간 범위의 항목을 효율적으로 조회하거나 제거할 수 있다.

구조
```text
key : user:queue:%s:wait, user:queue:%s:proceed
value : token
score : 시스템 현재 시간
```

- 이러한 구조를 통해 대기열의 순서, 처리 상태, 만료 시간 등을 효과적으로 관리할 수 있을거라 기대한다.

<br>

#### 2.3.3 대기열 관리 로직 구현

```java
@RequiredArgsConstructor
@Service
public class QueueService {
  private final String USER_QUEUE_WAIT_KEY = "user:queue:%s:wait";
  private final String USER_QUEUE_TOKEN = "user:queue:%s:%d";
  private final String USER_QUEUE_PROCEED_KEY = "user:queue:%s:proceed";

  private final StringRedisTemplate stringRedisTemplate;

  // 대기열을 등록한다.
  public QueueServiceDto registerUser(String queue, Long userId) {
    String token = createToken(queue, userId);
    long unixTimestamp = Instant.now().getEpochSecond();
    Boolean flag = stringRedisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY.formatted(queue), token, unixTimestamp);

    if (!flag) {
      throw new IllegalArgumentException("");
    }

    Long rank = stringRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), token);
    return new QueueServiceDto(token, rank >= 0 ? rank + 1 : rank);
  }

  // token 생성을 한다.
  private String createToken(String queue, Long userId) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      String input = USER_QUEUE_TOKEN.formatted(queue, userId);
      byte[] encodeHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

      StringBuilder hexString = new StringBuilder();
      for (byte hash : encodeHash) {
        hexString.append(String.format("%02x", hash));
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      log.error("queue token create error");
    }

    return "";
  }

  // wait 상태의 대기열에서 몇번째 순서인지 확인을 한다.
  public long getRankUser(String queue, String token) {
    Long rank = stringRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), token);
    if (rank == null) {
      return -1;
    }

    return rank >= 0 ? rank + 1 : rank;
  }

  // processing 상태의 대기열에 포함되어 있는지 확인한다.
  public boolean isAllowedUser(String queue, String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }

    Long rank = stringRedisTemplate.opsForZSet().rank(USER_QUEUE_PROCEED_KEY.formatted(queue), token);
    if (rank == null) {
      rank = -1L;
    }

    return rank >= 0;
  }

  /**
   *  진입 허용
   *  1. wait queue 사용자 제거
   *  2. proceed queue 사용자 추가
   */
  public Long allowUser(String queue, int count) {
    AtomicInteger result = new AtomicInteger();
    stringRedisTemplate.opsForZSet().popMin(USER_QUEUE_WAIT_KEY.formatted(queue), count).forEach(it -> {
      Boolean saveFlag = stringRedisTemplate.opsForZSet().add(USER_QUEUE_PROCEED_KEY.formatted(queue), it.getValue(), Instant.now().getEpochSecond());
      if (saveFlag) {
        result.getAndIncrement();
      }
    });

    return result.longValue();
  }
}
```

`registerUser`

- 목적 : 새로운 사용자를 대기열에 추가
- 동작 방식 : Redis의 ZADD 명령어를 사용하여 O(log N) 시간 복잡도로 추가
- 장점 : 대규모 동시 접속 상황에서도 빠른 대기열 등록 가능

`getRankUser`

- 목적 : 특정 사용자의 대기열 token 조회
- 동작 방식 : Redis의 ZRANK 명령어를 사용하여 현재 대기열 순위 검색
- 장점 : 빠른 대기열 순서 확인 가능

`isAllowedUser`

- 목적 : 특정 토큰의 proceed 대기열의 포함 여부를 확인
- 동작 방식 : Redis의 ZRANK 명령어를 사용, O(log N) 시간 복잡도
- 장점 : 대규모 대기열에서 빠른 위치 조회 가능

`allowUser`

- 목적 : wait 대기열에서 삭제 후, proceed 대기열로 이동
- 동작 방식 : count 개수 만큼 wait 대기열에서 꺼낸 후 proceed 대기열로 이동
- 장점 : 효율적인 배치 처리 구현 가능

<br>

#### 2.3.4 동시성 제어

- Redis의 원자적 연산을 활용한 동시성 관리
  - Redis는 단일 스레드 모델을 사용하여 명령어를 순차적으로 처리한다.
  - ZADD 명령어는 원자적으로 실행되어 동시에 여러 클라이언트가 같은 키에 접근하더라도 데이터 일관성이 보장된다.
  - 이를 통해 RDB와 같은 별도의 lock 매커니즘이 없어도 안전한 동시성 제어가 가능하다.

- 사용자별 중복 대기열 등록 방지
  - 토큰을 생성하는 createToken() 는 hash로 같은 input이 들어오면 항상 같은 output을 반환한다.
  - 따라서, 대기열 등록에서 같은 userId가 요청하면 기존 wait 대기열에 덮어씌어 진다.

<br>

#### 2.3.5 확장성 고려

- 처리 중인 대기열 수 제한 설정
  - 시스템의 처리 능력에 맞춰 동시에 처리할 수 있는 요청 수를 제한한다.
  - 이를 통해 시스템 과부하를 방지하고 안정적인 서비스 제공이 가능하다.
  - 한계 : 부하 테스트 없이 임의의 대기열 수 제한을 설정한 것으로, 보다 정확한 수치 산정은 부하 테스트 이후 가능하다.

- 대기열 처리 배치 크기 및 간격 설정을 통한 시스템 부하 관리
  - 처리 간격을 조절하여 시스템 부하를 분산시킬 수 있다.

<br>

#### 2.3.6 에러 처리 및 예외 상황 대비

- token 만료 시간 설정 및 관리
  - token에 만료 시간을 설정하여 무한정 대기하는 상황을 방지한다.

<br>

## 3) 주요 개선 사항
### 3.1 성능 향상

- 대기열 위치 조회 성능 대폭 개선 (O(log N) 시간 복잡도)
  - Redis의 Sorted Set 자료구조를 활용하여 대기열 구현
  - Sorted Set은 검색, 삽입, 삭제 연산이 모두 O(log N) 시간 복잡도를 가진다.
  - ex) 1000만 명의 대기열에서 위치를 조회할 때, Redis를 사용한 방식은 약 21번의 연산으로 위치를 찾을 수 있다.

- 실시간 대기열 상태 업데이트 가능
  - Redis의 인메모리 특성과 단일 스레드 모델을 활용하여 실시간 업데이트를 구현
  - Redis ZADD, ZRANK 등의 명령어는 원자적으로 실행되어 데이터 일관성을 보장하면서도 ms 단위의 빠른 응답 시간을 제공한다.

### 3.2 확장성 증가

- 대규모 동시 접속 처리 가능
  - Redis의 초당 수만 건의 연산 처리 능력을 활용하여 대규모 동시 접속 상황에서도 안정적인 서비스가 가능하다.
  - Redis Cluster를 통해 수평적 확장이 용이하여, 트래픽 증가에 따라 유연하게 시스템을 확장할 수 있다.

- 유연한 대기열 관리
  - Redis의 Sorted Ste을 사용하여 대기열을 구현함으로써, 대기열의 크기를 동적으로 조절할 수 있다.
  - 대기열 용량을 10만에서 100만으로 늘리는 데 별도의 스키마 변경이나 시스템 중단 없이 즉시 적용이 가능하다.

### 3.3 실시간성 확보

- 대기열 상태 변경 즉시 반영
  - Redis의 인메모리 특성으로 인해 디스크 I/O가 최소화되어, 상태 변경이 즉시 반영된다.
  - 사용자가 대기열에 진입하거나 빠져나갈 때 ms 단위의 지연으로 전체 대기열 상태가 업데이트 된다.

- 실시간 대기 시간 예측 기능 구현
  - 현재 대기열 위치와 처리 속도를 기반으로 한 로직을 통해 실시간으로 대기 시간을  예측합니다.
  - 이를 통해 사용자에게 더 정확한 예상 대기 시간 정보를 제공할 수 있고, 사용자 경험을 높일 수 있다.

## 4) 향후 개선 계획

- 부하 테스트
  - 시스템의 최대 처리 용량, 응답 시간, 병목 지점 등을 정확히 파악할 수 있을 것으로 기대
  - 부하 테스트 결과로 대기열 처리 속도, 배치 크기, 타임아웃 설정 등의 파라미터를 최적화할 수 있다.
