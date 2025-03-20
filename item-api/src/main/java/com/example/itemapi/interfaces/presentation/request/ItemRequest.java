package com.example.itemapi.interfaces.presentation.request;

import lombok.Builder;

import java.math.BigDecimal;

public record ItemRequest() {

    public record AddItem(
            String name,
            BigDecimal price,
            int stock
    ) {
        @Builder
        public AddItem {}
    }

    public record DecreaseStock(
            int decreaseCount
    ) {
    }
}
