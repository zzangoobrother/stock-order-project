package com.example.orderapi.interfaces.presentation;

import com.example.orderapi.application.service.OrderService;
import com.example.orderapi.interfaces.presentation.request.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/orders")
    public void createOrder(@RequestBody OrderRequest request) {
        orderService.createOrder(request.itemId(), request.quantity());
    }
}
