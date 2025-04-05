package com.example.orderapi.application.service;

import com.example.orderapi.domain.model.EventFailed;
import com.example.orderapi.domain.repository.EventFailedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventFailedService {

    private final EventFailedRepository eventFailedRepository;

    public void create(String type, String payload) {
        EventFailed eventFailed = EventFailed.builder()
                .eventType(type)
                .payload(payload)
                .build();

        eventFailedRepository.save(eventFailed);
    }
}
