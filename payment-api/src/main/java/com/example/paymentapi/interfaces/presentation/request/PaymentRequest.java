package com.example.paymentapi.interfaces.presentation.request;

import lombok.Builder;

public record PaymentRequest(
        String paymentType,
        String price
) {
    @Builder
    public PaymentRequest {}
}
