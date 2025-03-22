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
    participant ItemService
    participant Localcache
    participant Database
    
    User->>API: 제품 상세 조회 요청
    alt Localcache 존재
        API->>Localcache: 제품 상세 정보 조회
        Localcache-->>API: 제품 상세 정보
        API-->>User: 제품 상세 정보 반환
    else Localcache 미존재
        API->>ItemService: 제품 상세 조회 요청
        ItemService->>Database: 제품 상세 정보 조회
        Database-->>ItemService: 제품 상세 정보
        ItemService-->>Localcache: 제품 상세 정보 저장
        Localcache-->>API: 제품 상세 정보
        API-->>User: 제품 상세 정보 반환
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
    participant ItemService
    participant Localcache
    participant Database
    
    User->>API: 제품 상세 조회 요청
    alt Localcache 존재
        API->>Localcache: 제품 상세 정보 조회
        Localcache-->>API: 제품 상세 정보
        API-->>User: 제품 상세 정보 반환
    else Localcache 미존재
        API->>ItemService: 제품 상세 조회 요청
        ItemService->>Database: 제품 상세 정보 조회
        Database-->>ItemService: 제품 상세 정보
        ItemService-->>Localcache: 제품 상세 정보 저장
        Localcache-->>API: 제품 상세 정보
        API-->>User: 제품 상세 정보 반환
    end
```
