package com.example.itemapi.domain.manager;

import com.example.itemapi.domain.model.Item;
import com.example.itemapi.domain.repository.ItemRepository;
import com.example.itemapi.global.annotaion.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class ItemManager {

    private final ItemRepository itemRepository;

    public Item addItem(String name, BigDecimal price, int stock) {
        if (itemRepository.existsBy(name)) {
            throw new IllegalArgumentException("동일한 품목명이 존재합니다.");
        }

        Item item = Item.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .isDelete(false)
                .build();

        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    @Caching(cacheable = {
            @Cacheable(cacheManager = "localCacheManager", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId", sync = true),
            @Cacheable(cacheManager = "redisCacheManger", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId", sync = true)
    })
    public Item getBy(Long itemId) {
        return itemRepository.findByIdAndIsDeleteFalse(itemId).orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
    }

    @Transactional
    @Caching(put = {
            @CachePut(cacheManager = "localCacheManager", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId"),
            @CachePut(cacheManager = "redisCacheManger", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId")
    })
    @DistributedLock(key = "'decrease:stock:' + #itemId")
    public Item decreaseStock(Long itemId, int decreaseCount) {
        Item item = itemRepository.findByIdAndIsDeleteFalse(itemId).orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
        item.decreaseStock(decreaseCount);

        return item;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheManager = "localCacheManager", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId"),
            @CacheEvict(cacheManager = "redisCacheManger", cacheNames = "itemInfo", key = "'itemInfo:' + #itemId")
    })
    public void deleteBy(Long itemId) {
        Item item = itemRepository.findByIdAndIsDeleteFalse(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 품목이 존재하지 않습니다."));
        item.delete();
    }
}
