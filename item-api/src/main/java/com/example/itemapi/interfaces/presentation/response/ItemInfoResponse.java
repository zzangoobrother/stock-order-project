package com.example.itemapi.interfaces.presentation.response;

import com.example.itemapi.application.service.dto.ItemServiceDto;
import lombok.Builder;

public record ItemInfoResponse(
        Long itemId,
        String name,
        String price,
        int stock
) {

    @Builder
    public ItemInfoResponse {}

    public static ItemInfoResponse toItemInfoResponse(ItemServiceDto.ItemInfo itemInfo) {
        return ItemInfoResponse.builder()
                .itemId(itemInfo.itemId())
                .name(itemInfo.name())
                .price(itemInfo.price())
                .stock(itemInfo.stock())
                .build();
    }
}
