package com.example.itemapi.global.redis;

import com.example.itemapi.domain.manager.ItemManager;
import com.example.itemapi.global.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisSubscriber implements MessageListener {

    private final ItemManager itemManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Long itemId = ObjectMapperUtils.objectMapper().readValue(message.getBody(), Long.class);
            itemManager.getBy(itemId);
        } catch (IOException e) {
            log.error("redis subscriber 에러");
        }
    }
}
