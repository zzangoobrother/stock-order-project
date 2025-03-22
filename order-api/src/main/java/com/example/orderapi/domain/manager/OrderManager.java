package com.example.orderapi.domain.manager;

import com.example.orderapi.domain.model.Order;
import com.example.orderapi.domain.model.OrderStatus;
import com.example.orderapi.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class OrderManager {

    private final OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order createOrder(Long itemId, int quantity) {
        Order order = Order.builder()
                .itemId(itemId)
                .quantity(quantity)
                .orderStatus(OrderStatus.CREATED)
                .build();

        return orderRepository.save(order);
    }

    @Transactional
    public void paymentResult(Long orderId) {
        Order order = orderRepository.getBy(orderId).orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
        order.paymentResult();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failOrder(Long orderId) {
        Order order = orderRepository.getBy(orderId).orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
        order.failOrder();
    }
}
