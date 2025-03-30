package com.example.orderapi.kafka.dispatcher;

import com.example.kafka.OrderCompleteEvent;
import com.example.orderapi.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderCompleteDispatcher implements OrderDispatcher {

    private final OrderService orderService;

    @Override
    public boolean supports(Object event) {
        return event instanceof OrderCompleteEvent;
    }

    @Override
    public void execute(Object event) {
        OrderCompleteEvent orderCompleteEvent = (OrderCompleteEvent) event;
        orderService.orderComplete(orderCompleteEvent.getOrderId());
    }
}
