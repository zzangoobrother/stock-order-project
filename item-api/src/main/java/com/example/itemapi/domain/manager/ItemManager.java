package com.example.itemapi.domain.manager;

import com.example.itemapi.domain.model.Item;
import com.example.itemapi.domain.model.Price;
import com.example.itemapi.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class ItemManager {

    private final ItemRepository itemRepository;

    public void addItem(String name, int price, int stock) {
        Item item = Item.builder()
                .name(name)
                .price(new Price(BigDecimal.valueOf(price)))
                .stock(stock)
                .build();

        itemRepository.save(item);
    }
}
