# 재고 주문 관리 시스템 API 문서

## 목차
1. [제품 등록](#1-제품-등록)
2. [제품 상세 조회](#2-제품-상세-조회)
3. [제품 정보 수정](#3-제품-정보-수정)
4. [제품 삭제](#4-제품-삭제)
5. [주문하기](#5-주문하기)
6. [대기열 토큰 발급](#6-대기열-토큰-발급)
7. [토큰 대기열 정보 조회](#7-토큰-대기열-정보-조회)
8. [토큰 대기열 허용 여부 조회](#8-토큰-대기열-허용-여부-조회)

## 1. 제품 등록

### Description
- 제품을 등록합니다.
- 중복된 제품명 등록은 불가능합니다.

### Request

- **URL**: `/api/v1/items`
- **Method**: POST
- **Headers**:
    - `Content-Type`: application/json

- **Body**:
```json
{
  "name": "신발",
  "price": 10000,
  "stock": 10
}
```

### Response

```json
{
  "itemId": 1,
  "name": "신발",
  "price": "10000",
  "stock": 10
}
```

### Error
```json
{
  "code": 404,
  "message": "동일한 품목명이 존재합니다."
}
```

<br>

## 2. 제품 상세 조회

### Description
- 특정 제품 정보를 조회합니다.

### Request

- **URL**: `/api/v1/items/{itemId}`
- **Method**: GET
- **URL Params**:
  - `itemId`: Long (제품 ID)
- **Headers**:
  - `Content-Type`: application/json

### Response

```json
{
  "itemId": 1,
  "name": "신발",
  "price": "10000",
  "stock": 10
}
```

### Error
```json
{
  "code": 404,
  "message": "해당 품목이 존재하지 않습니다."
}
```

<br>

## 3. 제품 정보 수정

### Description
- 특정 제품 정보를 수정합니다.

### Request

- **URL**: `/api/v1/items/{itemId}`
- **Method**: PUT
- **URL Params**:
  - `itemId`: Long (제품 ID)
- **Headers**:
  - `Content-Type`: application/json

- **Body**:
```json
{
  "name": "신발",
  "price": 10000,
  "stock": 10
}
```

### Response

```json
{
  "itemId": 1,
  "name": "신발",
  "price": "10000",
  "stock": 10
}
```

### Error
```json
{
  "code": 404,
  "message": "해당 품목이 존재하지 않습니다."
}
```
```json
{
  "code": 404,
  "message": "동일한 품목명이 존재합니다."
}
```

<br>

## 4. 제품 삭제

### Description
- 특정 제품을 삭제합니다.

### Request

- **URL**: `/api/v1/items/{itemId}`
- **Method**: DELETE
- **URL Params**:
  - `itemId`: Long (제품 ID)
- **Headers**:
  - `Content-Type`: application/json

### Error
```json
{
  "code": 404,
  "message": "해당 품목이 존재하지 않습니다."
}
```

<br>

## 5. 주문하기

### Description
- 제품을 주문합니다.
- 같은 제품을 동시에 여러명의 손님이 주문할 수 있습니다.

### Request

- **URL**: `/api/v1/orders`
- **Method**: POST
- **Headers**:
  - `Content-Type`: application/json

- **Body**:
```json
{
  "itemId": "1",
  "quantity": 2
}
```

### Response

```json
{
  "orderId": 1,
  "name": "신발",
  "quantity": 2,
  "orderStatus": "COMPLETED"
}
```

### Error
```json
{
  "code": 404,
  "message": "해당 제품의 재고가 부족합니다."
}
```
```json
{
  "code": 500,
  "message": "결제에 실패했습니다. 다시 시도해 주세요."
}
```

<br>

## 6. 대기열 토큰 발급

### Description
- 대기열에 사용자를 추가하고 대기열 토큰을 반환합니다.

### Request

- **URL**: `/api/v1/queue`
- **Method**: GET
- **URL Params**:
  - `queue`: Long (대기열 종류)
  - `userId`: Long (사용자 ID)
- **Headers**:
  - `Content-Type`: application/json

### Response

```json
{
  "rank": 1
}
```

<br>

## 7. 토큰 대기열 정보 조회

### Description
- 사용자의 발급딘 대기열 토큰 순위를 조회합니다.

### Request

- **URL**: `/api/v1/queue/rank`
- **Method**: GET
- **URL Params**:
  - `queue`: Long (대기열 종류)
- **Headers**:
  - `Content-Type`: application/json

### Response

```json
{
  "rank": 1
}
```

<br>

## 8. 토큰 대기열 허용 여부 조회

### Description
- 사용자의 대기열 상태를 조회합니다.
- 클라이언트단에서 폴링으로 현재 대기열 상태를 조회하기 위해 사용합니다.

### Request

- **URL**: `/api/v1/queue/allowed`
- **Method**: GET
- **URL Params**:
  - `queue`: Long (대기열 종류)
  - `userId`: Long (사용자 ID)
- **Headers**:
  - `Content-Type`: application/json
