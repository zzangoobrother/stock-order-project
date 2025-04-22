package com.example.orderapi.interfaces.presentation;

import com.example.orderapi.application.service.OrderService;
import com.example.orderapi.interfaces.presentation.feign.QueueClient;
import com.example.orderapi.interfaces.presentation.request.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;
    private final QueueClient queueClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/orders")
    public void createOrder(@RequestBody OrderRequest request) {
        validateQueueProceed();
        orderService.createOrder(request.itemId(), request.quantity());
    }

    private void validateQueueProceed() {
        boolean result = queueClient.getBy("order");
        if (!result) {
            throw new IllegalArgumentException("토큰 확인 필요");
        }
    }
}
