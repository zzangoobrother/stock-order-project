package com.example.itemapi.application.service;

import org.springframework.stereotype.Service;

import com.example.itemapi.domain.model.EventFailed;
import com.example.itemapi.domain.repository.EventFailedRepository;

import lombok.RequiredArgsConstructor;

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
