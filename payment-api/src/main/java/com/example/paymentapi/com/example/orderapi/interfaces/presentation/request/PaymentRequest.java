package com.example.paymentapi.com.example.orderapi.interfaces.presentation.request;

import lombok.Builder;

public record PaymentRequest(
        String paymentType,
        String price
) {
    @Builder
    public PaymentRequest {}
}
