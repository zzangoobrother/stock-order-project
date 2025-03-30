package com.example.orderapi.kafka.dispatcher;

import com.example.kafka.OrderPaymentCompleteEvent;
import com.example.orderapi.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderPaymentCompleteDispatcher implements OrderDispatcher {

    private final OrderService orderService;

    @Override
    public boolean supports(Object event) {
        return event instanceof OrderPaymentCompleteEvent;
    }

    @Override
    public void execute(Object event) {
        OrderPaymentCompleteEvent orderPaymentCompleteEvent = (OrderPaymentCompleteEvent) event;
        orderService.paymentComplete(orderPaymentCompleteEvent.getOrderId(), orderPaymentCompleteEvent.getItemId(), orderPaymentCompleteEvent.getDecreaseCount());
    }
}
