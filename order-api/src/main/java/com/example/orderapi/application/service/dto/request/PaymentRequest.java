package com.example.orderapi.application.service.dto.request;

import lombok.Builder;

public record PaymentRequest(
        String paymentType,
        String price
) {
    @Builder
    public PaymentRequest {}
}
