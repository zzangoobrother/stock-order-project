## 1. 제품 등록

### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant ItemService
    participant Database
    
    User->>API: 제품 등록 요청
    API->>ItemService: 중복 제품명 검증
    ItemService-->>API: 중복 결과
    alt 중복 없음 확인
        API->ItemService: 제품 생성
        ItemService->>Database: 제품 저장
        Database-->>ItemService: 저장 완료
        ItemService-->>API: 생성된 제품 정보
        API-->>User: 생성된 제품 정보 반환
    else 중복 존재 확인
        API-->>User: 오류 메시지 반환 (Duplication Item Name)
    end
```

### Description

유저가 제품을 등록합니다.

제품 생성을 요청하고 제품 정보를 DB에 저장합니다.

생성된 제품의 정보를 반환합니다.

<br>

## 2. 제품 상세 조회

### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant Localcache
    participant Redis
    participant ItemService
    participant Database
    
    User->>API: 제품 상세 조회 요청
    API->>Localcache: 제품 상세 정보 조회
    alt Localcache 존재
        Localcache-->>API: 제품 상세 정보
        API-->>User: 제품 상세 정보 반환            
    else Localcache 미존재
        Localcache->>Redis: 제품 상세 정보 조회
        alt Redis 존재
            Redis-->>Localcache: 제품 상세 정보 저장
            Localcache-->>API: 제품 상세 정보
            API-->>User: 제품 상세 정보 반환
        else Redis 미존재
            Redis->>ItemService: 제품 상세 조회 요청
            ItemService->>Database: 제품 상세 정보 조회
            Database-->>ItemService: 제품 상세 정보
            ItemService-->>Redis: 제품 상세 정보 저장
            Redis-->>Localcache: 제품 상세 정보 저장
            Localcache-->>API: 제품 상세 정보
            API-->>User: 제품 상세 정보 반환
        end
    end
```

### Description

제품 상세 정보를 조회합니다.

<br>

## 3. 제품 정보 수정

### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant Localcache
    participant Redis
    participant ItemService
    participant Database
    
    User->>API: 제품 정보 수정 요청
    API->>Localcache: 제품 정보 수정 요청
    Localcache->>ItemService: 제품 정보 수정 요청
    ItemService->>Database: 제품 정보 수정
    Database-->>ItemService: 수정된 제품 정보
    alt Redis key 존재
        ItemService-->>Redis: 수정된 제품 정보 cache에 업데이트
        alt Localcache Key 존재
            Redis-->>Localcache: 수정된 제품 정보 cache에 업데이트
            Localcache-->>API: 제품 정보 수정 반환
            API-->>User: 수정된 제품 정보 반환
        else Localcache Key 미존재
            Redis-->>Localcache: 수정된 제품 정보 cache에 저장
            Localcache-->>API: 제품 정보 수정
            API-->>User: 수정된 제품 정보 반환
        end
    else Redis key 미존재
        ItemService-->>Redis: 수정된 제품 정보 cache에 저장
        alt Localcache Key 존재
            Redis-->>Localcache: 수정된 제품 정보 cache에 업데이트
            Localcache-->>API: 제품 정보 수정 반환
            API-->>User: 수정된 제품 정보 반환
        else Localcache Key 미존재
            Redis-->>Localcache: 수정된 제품 정보 cache에 저장
            Localcache-->>API: 제품 정보 수정
            API-->>User: 수정된 제품 정보 반환
        end
    end
```

### Description

제품 상세 정보를 수정합니다.

<br>

## 4. 제품 삭제

### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant Localcache
    participant Redis
    participant ItemService
    participant Database
    
    User->>API: 제품 삭제 요청
    API->>Localcache: 제품 정보 cache 삭제
    Localcache->>Redis: 제품 정보 cache 삭제
    Redis->>ItemService: 제품 삭제 요청
    ItemService->>Database: 제품 소프트 delete 처리
    Database-->>ItemService: 제품 삭제 처리 완료
    ItemService-->>Redis: 제품 삭제 처리 요청
    Redis-->>Localcache: 제품 삭제 처리 요청
    Localcache-->>API: 제품 삭제 처리 완료 반환
    API-->>User: 제품 삭제 처리 완료 반환
```

### Description

제품을 삭제합니다.

<br>

## 4. 주문하기

### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant OrderService
    participant ItemService
    participant PaymentService
    participant Database
    
    User->>API: 주문 요청
    API->>OrderService: 주문 처리 요청
    OrderService->>ItemService: 제품 재고 확인
    ItemService->>Database: 제품 재고 조회
    Database-->>ItemService: 제품 재고 반환
    ItemService-->>OrderService: 재고 확인 결과
    alt 재고 충분함
        OrderService->>Database: 주문 생성
        Database-->>OrderService: 주문 생성 완료
        OrderService->>PaymentService: 결제하기
        PaymentService->>Database: 결제 생성
        Database-->>PaymentService: 결제 생성 완료
        PaymentService-->>OrderService: 결제 성공 및 결제 내역 반환
        OrderService-->>API: 주문 완료 반환
        API-->>User: 주문 완료 반환
        OrderService->>OrderService: 주문 결제 완료 상태 변경
        OrderService->>ItemService: 재고 차감 요청
        ItemService->>Database: 제품 재고 차감
        Database-->>ItemService: 제품 재고 차감 완료
        ItemService->>OrderService: 주문 완료 상태 변경 요청
    else 재고 부족함
        OrderService-->>API: 재고 부족 오류 메시지 반환
        API-->>User: 오류 메시지 반환
    end
```

### Description

주문 하기를 합니다.
결제를 진행합니다.
결제에 성공하면 주문 상태를 결제 완료 상태로 변경합니다.

<br>

## 5. 대기열 토큰 발급

### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant QueueService
    participant Redis
    
    User->>API: 토큰 발급 요청
    API->>QueueService: 토큰 생성 요청
    QueueService->>QueueService: 토큰 생성
    QueueService->>Redis: 토큰 정보 저장
    Redis-->>QueueService: 저장 완료
    QueueService-->>Redis: 현재 대기열 상태 요청
    Redis-->>QueueService: 현재 대기열 상태
    QueueService-->>API: 대기열 정보 (토큰, 순서)
    API-->>User: 대기열 정보 반환 (토큰, 순서)
```

### Description

유저가 주문을 시도할 때, 토큰을 발급받습니다.  
현재 대기열의 상태를 조회하고, 토큰 생성을 요청하여 Redis에 저장합니다.  
생성된 토큰과 조회한 대기열의 상태 정보를 반환합니다.

<br>

## 6. 토큰 대기열 정보 조회

### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant QueueService
    participant Redis
    
    User->>API: 대기열 정보 조회 요청(토큰 포함)
    API->>QueueService: 현재 대기열 상태 요청
    alt 쿠키 토큰 존재
        QueueService->>Redis: 대기열 정보 조회(현재 순위)
        Redis-->>QueueService: 대기열 정보
        QueueService-->>API: 사용자의 현재 대기 상태
        API-->>User: 대기열 정보
    else 쿠키 토큰 미존재
        API-->>User: 오류 메시지 반환
    end
```

### Description

토큰을 통해 대기열 정보를 조회합니다.  
폴링으로 대기열을 확인하는 것을 전제합니다.

<br>

## 7. 토큰 대기열 허용 여부 조회

### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant QueueService
    participant Redis
    
    User->>API: 토큰 허용 여부 조회 요청(토큰 포함)
    API->>QueueService: 토큰 허용 여부 요청
    alt 쿠키 토큰 존재
        QueueService->>Redis: 진행 대기열 토큰 존재 조회
        Redis-->>QueueService: 토큰 존재 여부
        QueueService-->>API: 사용자의 토큰 존재 여부
        API-->>User: 토큰 존재 true/false
    else 쿠키 토큰 미존재
        API-->>User: 오류 메시지 반환
    end
```

### Description

토큰을 통해 허용된 토큰 집합의 존재 유무를 조회합니다.

<br>
