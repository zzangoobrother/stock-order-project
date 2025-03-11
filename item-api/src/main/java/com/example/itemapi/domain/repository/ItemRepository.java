package com.example.itemapi.domain.repository;

import com.example.itemapi.domain.model.Item;

public interface ItemRepository {
    Item save(Item item);
}
