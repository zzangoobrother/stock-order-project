package com.example.itemapi.interfaces.presentation.request;

import lombok.Builder;

public record ItemRequest() {

    public record AddItem(
            String name,
            int price,
            int stock
    ) {
        @Builder
        public AddItem {}
    }
}
