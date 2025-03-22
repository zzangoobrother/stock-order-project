package com.example.orderapi.application.service.dto.request;

import lombok.Builder;

public record PaymentCancelRequest(
        String paymentType,
        String price
) {
    @Builder
    public PaymentCancelRequest {}
}
