package com.example.itemapi.application.service;

import com.example.itemapi.application.service.dto.ItemServiceDto;
import com.example.itemapi.domain.manager.ItemManager;
import com.example.itemapi.domain.model.Item;
import com.example.itemapi.global.config.TopicNames;
import com.example.itemapi.global.redis.RedisPublisher;
import com.example.itemapi.interfaces.presentation.feign.ItemClient;
import com.example.itemapi.kafka.OrderEventProducer;
import com.example.kafka.OrderCompleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemManager itemManager;
    private final OrderEventProducer orderEventProducer;
    private final RedisPublisher redisPublisher;
    private final ItemClient itemClient;

    /**
     * - 제품을 추가한다.
     * - 같은 제품명이 있는지 확인한다.
     */
    public ItemServiceDto.CreateItem addItem(String name, BigDecimal price, int stock) {
        Item item = itemManager.addItem(name, price, stock);

        try {
            redisPublisher.publish("item-cache", item.getId());
        } catch (RuntimeException e) {
            log.error("Redis pub/sub 오류, api 대체");
            log.error("error log : {}", e);
            itemClient.getBy(item.getId());
        }

        return new ItemServiceDto.CreateItem(item.getId());
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
    public void decreaseStock(Long orderId, Long itemId, int decreaseStock) {
        itemManager.decreaseStock(itemId, decreaseStock);

        orderEventProducer.sendResultEvent(TopicNames.ORDER_RESULT_TOPIC, new OrderCompleteEvent(orderId));
    }

    /**
     * 1. 제품 삭제
     * 2. 로컬 캐시 삭제
     */
    public void deleteBy(Long itemId) {
        itemManager.deleteBy(itemId);
    }
}
