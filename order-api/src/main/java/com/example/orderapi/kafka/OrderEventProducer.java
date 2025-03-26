package com.example.orderapi.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.kafka.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderEventProducer {
	private static String DECREASE_STOCK = "item-decrease-stock-result";

	private final KafkaTemplate<String, Event> kafkaTemplate;

	public void sendResultEvent(Object event) {
		kafkaTemplate.send(DECREASE_STOCK, new Event(event.getClass().getName(), event));
	}
}
