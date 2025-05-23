package com.example.itemapi.acceptance.global;

import com.example.kafka.Event;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Profile("test")
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	private static final String BOOTSTRAP_SERVER = "localhost:9092";

	private static final String SCHEMA_REGISTRY_URL_CONFIG = "schema.registry.url";
	private static final String SCHEMA_REGISTRY_URL = "http://localhost:9001";

	@Bean
	public ConsumerFactory<String, Event> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		config.put(SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
		config.put("specific.avro.reader", true);

		return new DefaultKafkaConsumerFactory<>(config);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Event> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Event> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setCommonErrorHandler(errorHandler());

		ContainerProperties containerProperties = factory.getContainerProperties();
		containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

		return factory;
	}

	private DefaultErrorHandler errorHandler() {
		return new DefaultErrorHandler((record, e) -> {
		}, new FixedBackOff(1000L, 3L));
	}
}
