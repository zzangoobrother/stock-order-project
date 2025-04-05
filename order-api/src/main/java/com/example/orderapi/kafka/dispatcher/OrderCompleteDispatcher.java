package com.example.orderapi.kafka.dispatcher;

import com.example.kafka.OrderCompleteEvent;
import com.example.orderapi.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderCompleteDispatcher implements OrderDispatcher<OrderCompleteEvent> {

    private final OrderService orderService;

    @Override
    public boolean supports(String event) {
        return event.equals(OrderCompleteEvent.class.getName());
    }

    @Override
    public void execute(OrderCompleteEvent event) {
        orderService.orderComplete(event.getOrderId());
    }
}
