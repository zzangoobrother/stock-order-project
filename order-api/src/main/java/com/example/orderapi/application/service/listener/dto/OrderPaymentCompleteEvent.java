package com.example.orderapi.application.service.listener.dto;

public record OrderPaymentCompleteEvent(
        Long orderId,
        Long itemId,
        int decreaseCount
) {
}
