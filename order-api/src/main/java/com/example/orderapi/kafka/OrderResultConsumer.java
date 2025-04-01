package com.example.orderapi.kafka;

import com.example.kafka.Event;
import com.example.kafka.OrderCompleteEvent;
import com.example.orderapi.application.service.OrderService;
import com.example.orderapi.global.config.TopicNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderResultConsumer {

	private final OrderService orderService;

	@KafkaListener(topics = TopicNames.ORDER_COMPLETE_TOPIC, groupId = "order-group")
	public void onCommandEvent(ConsumerRecord<String, Event> record) {
		log.info("Publish command event: {}", record.value());
		Object event = record.value().getEvent();

		if (event instanceof OrderCompleteEvent) {
			OrderCompleteEvent orderCompleteEvent = (OrderCompleteEvent) event;
			orderService.orderComplete(orderCompleteEvent.getOrderId());
		}
	}
}
