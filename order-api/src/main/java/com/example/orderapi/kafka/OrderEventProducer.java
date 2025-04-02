package com.example.orderapi.kafka;

import com.example.kafka.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderEventProducer {

	private final KafkaTemplate<String, Event> kafkaTemplate;

	public void sendEvent(String topicName, Object event) {
		kafkaTemplate.send(topicName, new Event(event.getClass().getName(), event));
	}
}
