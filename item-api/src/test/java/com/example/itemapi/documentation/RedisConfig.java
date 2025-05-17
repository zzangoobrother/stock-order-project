package com.example.itemapi.documentation;

import com.example.itemapi.global.redis.RedisSubscriber;
import com.example.itemapi.global.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Profile("test")
@Configuration
public class RedisConfig {

    @Autowired
    private RedisSubscriber redisSubscriber;

    @Value("${spring.data.cluster.redis.host1}")
    String host1;
    @Value("${spring.data.cluster.redis.port1}")
    int port1;
//    @Value("${spring.data.cluster.redis.host2}")
//    String host2;
//    @Value("${spring.data.cluster.redis.port2}")
//    int port2;
//    @Value("${spring.data.cluster.redis.host3}")
//    String host3;
//    @Value("${spring.data.cluster.redis.port3}")
//    int port3;
//    @Value("${spring.data.cluster.redis.host4}")
//    String host4;
//    @Value("${spring.data.cluster.redis.port4}")
//    int port4;
//    @Value("${spring.data.cluster.redis.host5}")
//    String host5;
//    @Value("${spring.data.cluster.redis.port5}")
//    int port5;
//    @Value("${spring.data.cluster.redis.host6}")
//    String host6;
//    @Value("${spring.data.cluster.redis.port6}")
//    int port6;

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(new RedisClusterConfiguration()
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
                .clusterNode(host1, port1)
        );
    }

    @Bean
    RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory lettuceConnectionFactory) {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        ObjectMapper mapper = ObjectMapperUtils.objectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));

        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory());
        container.addMessageListener(messageListenerAdapter(), topic());
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(redisSubscriber);
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("item-cache");
    }
}
