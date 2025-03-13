package com.example.itemapi.application.service.dto;

import com.example.itemapi.domain.model.Item;
import lombok.Builder;

public record ItemServiceDto() {

    public record ItemInfo(
            Long itemId,
            String name,
            String price,
            int stock
    ) {
        @Builder
        public ItemInfo {}

        public static ItemInfo toItemInfo(Item item) {
            return ItemServiceDto.ItemInfo.builder()
                    .itemId(item.getId())
                    .name(item.getName())
                    .price(item.getPriceToString())
                    .stock(item.getStock())
                    .build();
        }
    }
}
