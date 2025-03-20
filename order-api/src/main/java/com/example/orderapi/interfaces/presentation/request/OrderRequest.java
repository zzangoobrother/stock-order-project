package com.example.orderapi.interfaces.presentation.request;

public record OrderRequest(
        Long itemId,
        int quantity
) {
}
