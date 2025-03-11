package com.example.itemapi.application.service;

import com.example.itemapi.domain.manager.ItemManager;
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
}
