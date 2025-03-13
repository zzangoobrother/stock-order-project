package com.example.itemapi.domain.manager;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.itemapi.domain.model.Item;
import com.example.itemapi.domain.model.Price;
import com.example.itemapi.domain.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ItemManager {

    private final ItemRepository itemRepository;

    public void addItem(String name, int price, int stock) {
        if (itemRepository.existsBy(name.trim())) {
            throw new IllegalArgumentException("동일한 품목명이 존재합니다.");
        }

        Item item = Item.builder()
                .name(name)
                .price(new Price(BigDecimal.valueOf(price)))
                .stock(stock)
                .build();

        itemRepository.save(item);

        // TODO : 캐시 동기화 적용 해야함, 로컬 캐시 동기화 예정이라 Redis pub/sub 사용 예정
    }

    public Item getBy(Long itemId) {
        return itemRepository.findByItemId(itemId).orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
    }
}
