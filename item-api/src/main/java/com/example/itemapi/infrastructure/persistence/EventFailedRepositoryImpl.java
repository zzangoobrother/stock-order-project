package com.example.itemapi.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.example.itemapi.domain.model.EventFailed;
import com.example.itemapi.domain.repository.EventFailedRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class EventFailedRepositoryImpl implements EventFailedRepository {

    private final EventFailedJpaRepository repository;


    @Override
    public EventFailed save(EventFailed eventFailed) {
        return repository.save(eventFailed);
    }
}
