package com.example.orderapi.application.service.dto.response;

public record ItemInfoResponse(
        Long itemId,
        String name,
        String price,
        int stock
) {
}
