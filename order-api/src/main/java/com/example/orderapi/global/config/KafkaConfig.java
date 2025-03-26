package com.example.orderapi.global.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.example.kafka.Event;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@EnableKafka
@Configuration
public class KafkaConfig {

	private static final String BOOTSTRAP_SERVER = "localhost:10000";

	@Bean
	public ProducerFactory<String, Event> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		config.put("schema.registry.url", "http://localhost:9001");

		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, Event> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public List<NewTopic> topics() {
		return List.of(
			new NewTopic("item-decrease-stock-result", 1, (short)1)
		);
	}
}
