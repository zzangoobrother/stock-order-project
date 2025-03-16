package com.example.itemapi.domain.repository;

import com.example.itemapi.domain.model.Item;

import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    boolean existsBy(String name);

    Optional<Item> findByItemId(Long itemId);
}
