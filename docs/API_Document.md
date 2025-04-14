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
