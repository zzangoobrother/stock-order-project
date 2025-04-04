package com.example.itemapi.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.itemapi.application.service.EventFailedService;
import com.example.itemapi.application.service.ItemService;
import com.example.itemapi.global.config.TopicNames;
import com.example.kafka.DecreaseStockEvent;
import com.example.kafka.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemResultConsumer {

	private final ItemService itemService;
	private final EventFailedService eventFailedService;

	@KafkaListener(topics = TopicNames.ITEM_DECREASE_STOCK_TOPIC, groupId = "item-group", containerFactory = "kafkaListenerContainerFactory")
	public void onCommandEvent(ConsumerRecord<String, Event> record) {
		log.info("Publish command event: {}", record.value());
		Object event = record.value().getEvent();

		if (event instanceof DecreaseStockEvent) {
			DecreaseStockEvent decreaseStockEvent = (DecreaseStockEvent)event;
			itemService.decreaseStock(decreaseStockEvent.getOrderId(), decreaseStockEvent.getItemId(), decreaseStockEvent.getDecreaseCount());
		}
	}

	@KafkaListener(topics = TopicNames.ITEM_DECREASE_STOCK_DLT_TOPIC, groupId = "item-group", containerFactory = "kafkaListenerContainerFactory")
	public void onDLTEvent(ConsumerRecord<String, Event> record) {
		Event event = record.value();
		log.info("DLT Publish command event: {}", event);

		eventFailedService.create(event.getEventType(), event.getEvent().toString());
	}
}
