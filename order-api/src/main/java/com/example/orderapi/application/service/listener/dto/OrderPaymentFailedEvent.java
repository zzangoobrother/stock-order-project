package com.example.orderapi.application.service.listener.dto;

public record OrderPaymentFailedEvent(
        String eventType,
        String payload
) {
}
