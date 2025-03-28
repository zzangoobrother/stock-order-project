package com.example.orderapi.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.kafka.Event;
import com.example.orderapi.global.config.TopicNames;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderEventProducer {

	private final KafkaTemplate<String, Event> kafkaTemplate;

	public void sendResultEvent(Object event) {
		kafkaTemplate.send(TopicNames.ITEM_DECREASE_STOCK_TOPIC, new Event(event.getClass().getName(), event));
	}
}
