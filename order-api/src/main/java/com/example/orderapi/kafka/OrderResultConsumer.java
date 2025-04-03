package com.example.orderapi.kafka;

import com.example.kafka.Event;
import com.example.orderapi.global.config.TopicNames;
import com.example.orderapi.kafka.dispatcher.OrderDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderResultConsumer {

	private final List<OrderDispatcher> orderDispatchers;

	@KafkaListener(topics = TopicNames.ORDER_RESULT_TOPIC, groupId = "order-group", containerFactory = "kafkaListenerContainerFactory")
	public void onCommandEvent(ConsumerRecord<String, Event> record) {
		Event event = record.value();
		log.info("Publish command event: {}", event);

		orderDispatchers.stream()
				.filter(it -> it.supports(event.getEventType()))
				.findFirst()
				.ifPresentOrElse(
						it -> it.execute(event.getEvent()),
						() -> log.warn("해당 이벤트를 처리할 수 없습니다.")
				);
	}

	@KafkaListener(topics = TopicNames.ORDER_DLT_TOPIC, groupId = "order-group", containerFactory = "kafkaListenerContainerFactory")
	public void onCommandEvent2(ConsumerRecord<String, Event> record) {
		Event event = record.value();
		log.info("DLT Publish command event: {}", event);
	}
}
