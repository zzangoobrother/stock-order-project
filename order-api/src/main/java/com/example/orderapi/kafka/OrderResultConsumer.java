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

	@KafkaListener(topics = TopicNames.ORDER_RESULT_TOPIC, groupId = "order-group")
	public void onCommandEvent(ConsumerRecord<String, Event> record) {
		log.info("Publish command event: {}", record.value());
		Object event = record.value().getEvent();

		orderDispatchers.stream()
				.filter(it -> it.supports(event))
				.findFirst()
				.ifPresentOrElse(
						it -> it.execute(event),
						() -> log.warn("해당 이벤트를 처리할 수 없습니다.")
				);
	}
}
