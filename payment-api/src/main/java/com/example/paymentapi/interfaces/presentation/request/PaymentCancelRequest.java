package com.example.paymentapi.interfaces.presentation.request;

import lombok.Builder;

public record PaymentCancelRequest(
        String paymentType,
        String price
) {
    @Builder
    public PaymentCancelRequest {}
}
