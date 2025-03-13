package com.example.itemapi.application.service;

import com.example.itemapi.application.service.dto.ItemServiceDto;
import com.example.itemapi.domain.manager.ItemManager;
import com.example.itemapi.domain.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemManager itemManager;

    /**
     * - 제품을 추가한다.
     * - 같은 제품명이 있는지 확인한다.
     */
    public void addItem(String name, int price, int stock) {
        itemManager.addItem(name, price, stock);
    }

    /**
     * 1. 로컬 캐시를 통해 조회를 한다
     * 2. 로컬 캐시에 없다면 Redis를 통해 조회를 한다.
     * 3. Redis에도 없다면 DB에서 조회를 한다.
     */
    public ItemServiceDto.ItemInfo getBy(Long itemId) {
        Item item = itemManager.getBy(itemId);
        return ItemServiceDto.ItemInfo.toItemInfo(item);
    }
}
