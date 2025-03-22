package com.example.orderapi.application.service;

import com.example.orderapi.application.service.dto.request.DecreaseStockRequest;
import com.example.orderapi.application.service.dto.request.PaymentRequest;
import com.example.orderapi.application.service.dto.response.ItemInfoResponse;
import com.example.orderapi.domain.manager.OrderManager;
import com.example.orderapi.domain.model.Order;
import com.example.orderapi.interfaces.presentation.feign.ItemClient;
import com.example.orderapi.interfaces.presentation.feign.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderManager orderManager;
    private final ItemClient itemClient;
    private final PaymentClient paymentClient;

    /**
     * 주문 생각해보기
     * 주문 > 결제 (rest api 통신) > 주문 완료
     * rest api 통신 사용 이유 : 결제 후 사용자에게 주문 완료라는 response를 보내기 위해 선택
     * 주문 상태 : 주문 생성, 결제 완료, 주문 완료
     * 위와 같은 상태를 한 이유는 만약 주문 중 db가 멈추거나, 결제 중 서비스가 멈춘다면? 어떻게 해야 할까? 라는 고민에서 나온 결과
     * 배치나 스케줄링을 통해 주문 생성 상태인 주문은 결제를 할 수 있다.
     * 근데 만약 결제는 완료된 상태인데 주문 생성 상태인 경우는?
     * 그런 경우는 일단 결제 api 서비스를 만들면서 고민해보자.
     */
    @Transactional
    public void createOrder(Long itemId, int quantity) {
        // 제품 재고 검증
        ItemInfoResponse itemInfoResponse = itemClient.getBy(itemId);
        if (itemInfoResponse.stock() <= quantity) {
            throw new IllegalArgumentException("해당 제품의 재고가 부족합니다.");
        }

        // 주문 생성
        Order order = orderManager.createOrder(itemId, quantity);

        // 결제
        try {
            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .paymentType("카드")
                    .price("10000")
                    .build();
            paymentClient.payment(paymentRequest);
        } catch (RuntimeException e) {
            // 결제 실패
            orderManager.failOrder(order.getId());
            throw new IllegalStateException("결제에 실패했습니다. 다시 시도해 주세요.");
        }

        // 주문 결제 완료 상태 변경
        orderManager.paymentResult(order.getId());

        // 재고 차감
        itemClient.decreaseStock(itemId, new DecreaseStockRequest(quantity));
    }
}
