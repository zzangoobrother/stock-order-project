package com.example.itemapi.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.itemapi.application.service.ItemService;
import com.example.kafka.DecreaseStockEvent;
import com.example.kafka.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemResultConsumer {

	private final ItemService itemService;

	@KafkaListener(topics = "item-decrease-stock-result", groupId = "item-group")
	public void onCommandEvent(ConsumerRecord<String, Event> record) {
		log.info("Publish command event: {}", record.value());
		Object event = record.value().getEvent();

		if (event instanceof DecreaseStockEvent) {
			DecreaseStockEvent decreaseStockEvent = (DecreaseStockEvent)event;
			itemService.decreaseStock(decreaseStockEvent.getItemId(), decreaseStockEvent.getDecreaseCount());
		}
	}
}
