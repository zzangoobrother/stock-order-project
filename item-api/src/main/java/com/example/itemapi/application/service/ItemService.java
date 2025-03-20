package com.example.itemapi.application.service;

import com.example.itemapi.application.service.dto.ItemServiceDto;
import com.example.itemapi.domain.manager.ItemManager;
import com.example.itemapi.domain.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemManager itemManager;

    /**
     * - 제품을 추가한다.
     * - 같은 제품명이 있는지 확인한다.
     */
    public void addItem(String name, BigDecimal price, int stock) {
        itemManager.addItem(name, price, stock);
    }

    /**
     * 1. 로컬 캐시를 통해 조회를 한다
     * 2. 로컬 캐시에 없다면 DB에서 조회를 한다.
     */
    public ItemServiceDto.ItemInfo getBy(Long itemId) {
        Item item = itemManager.getBy(itemId);
        return ItemServiceDto.ItemInfo.toItemInfo(item);
    }

    /**
     * 1. 제품 재고 차감
     * 2. 재고가 부족하다면 예외 처리
     */
    @Transactional
    public void decreaseStock(Long itemId, int decreaseStock) {
        itemManager.decreaseStock(itemId, decreaseStock);
    }

    /**
     * 1. 제품 삭제
     * 2. 로컬 캐시 삭제
     */
    public void deleteBy(Long itemId) {
        itemManager.deleteBy(itemId);
    }
}
