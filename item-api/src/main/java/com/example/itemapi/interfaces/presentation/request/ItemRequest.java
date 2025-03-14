package com.example.itemapi.interfaces.presentation.request;

import java.math.BigDecimal;

import lombok.Builder;

public record ItemRequest() {

    public record AddItem(
            String name,
            BigDecimal price,
            int stock
    ) {
        @Builder
        public AddItem {}
    }
}
