# 동시성 문제와 해결

## 1. 재고 차감에서 동시성 문제가 발생할 수 있는 로직

### 1) 재고 차감 기능
- 동시에 여러명이 하나의 제품의 재고를 차감하려 한다면 한명씩 재고 차감을 할 수 있도록 해야한다.

#### 동시성 문제 발생 이유 :
- 여러 사용자가 동시에 같은 제품의 재고를 차감하려고 시도하면, 시스템이 각 요청을 순차적으로 처리하지 않으면 중복 재고 차감이 발생할 수 있다.
- 데이터베이스 트랜잭션이 적절히 관리되지 않으면, 한 사용자의 재고 차감 과정 중 다른 사용자가 같은 제품의 재고 차감을 할 수 있다.

#### 기대하는 결과 :
- 특정 제품에 대해 재고 차감 요청을 완료한 사용자만 해당 제품 재고를 성공적으로 차감을 한다.
- 다른 사용자들의 동일한 제품 재고 차감 시도는 잠시 대기하고, 재고 차감이 완료되면 다음 사용자가 재고를 차감한다.

### 2)

<br>

## 2. 동시성 이슈 대응 이전의 로직

- 원래 로직은 Service 계층에 @Transactional 어노테이션을 적용하여 트랜잭션을 관리했다.
- 이러한 접근 방식을 선택한 이유는 아래와 같다.

### 트랜잭션 관리 방식 선택 이유

1. 원자성 보장 : 하나의 트랜잭션 내에서 Service 레이어의 모든 로직이 원자성을 가지고 실행되어야 한다고 판단했다.
2. 단순성 : Service 계층에 트랜잭션을 적용함으로써 모든 데이터베이스 연산이 하나의 트랜잭션으로 묶이도록 했다.
3. 일관성 : 모든 비즈니스 로직이 하나의 트랜잭션 내에서 실행되므로, 데이터의 일관성을 유지하기 쉽다고 생각했다.

### 코드 설명

```java
// 재고 차감

@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemManager itemManager;

    @Transactional
    public void decreaseStock(Long orderId, Long itemId, int decreaseStock) {
       itemManager.decreaseStock(itemId, decreaseStock);
    }
}

@RequiredArgsConstructor
@Component
public class ItemManager {
    private final ItemRepository itemRepository;
    
    public Item decreaseStock(Long itemId, int decreaseCount) {
       Item item = itemRepository.findByIdAndIsDeleteFalse(itemId).orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
       item.decreaseStock(decreaseCount);

       return item;
    }
}
```

### 재고 차감 로직 설명

1. ItemService 클래스 :
    - decreaseStock() 메서드에 @Transactional 어노테이션이 적용되어 있다.
    - Facade의 역할을 한다.

2. ItemManager 클래스 :
    - decreaseStock() 메서드에서 재고 차감을 수행한다.
    - 제품 정보를 조회한다.
    - 제품 재고 차감전 제품 재고 < decreaseCount 유효성 검사를 한다.
    - 제품 재고를 차감한다.

### 이 접근 방식의 문제점

1. 트랜잭션 범위가 너무 넓다 :
    - 서비스 계층의 메서드 전체가 하나의 트랜잭션으로 묶여 있어, 불필요하게 긴 시간 동안 데이터베이스 리소스를 점유할 수 있다.

2. 동시성 제어의 어려움 :
    - 넓은 트랜잭션 범위로 인해 동시에 여러 요청이 처리될 때 데드락이 발생하거나 성능이 저하될 수 있다.

3. 세밀한 제어의 부재 :
    - 특정 연산에 대해서만 트랜잭션을 적용하거나, 다른 격리 수준을 설정하는 등의 세밀한 제어가 어렵다.

4. 성능 저하 :
    - 모든 연산이 하나의 큰 트랜잭션으로 묶여 있어, 데이터베이스 연결이 오래 유지되면서 전반적인 시스템 성능이 저하될 수 있다.

이러한 문제점들로 인해 동시성 이슈가 발생할 가능성이 높아지며, 특히 높은 트래픽 상황에서 시스템의 안정성과 성능이 저하될 수 있다.  
따라서 동시성 문제에 대응하기 위해서는 트랜잭션의 범위를 좁히고, 더 세밀한 동시성 제어 메커니즘을 도입할 필요가 있다.

## 3. DB 락 구현

### 낙관적 락 (Optimistic Lock)

- 낙관적 락은 동시 업데이트가 드물게 발생한다는 가정 하에 동작한다.
- 이 방식은 데이터 수정 시 충돌이 발생하지 않을 것이라고 이름 그대로 '낙관적으로' 가정하고, 충돌이 발생했을 때 이를 감지하고 처리한다.
