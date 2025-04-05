package com.example.orderapi.infrastructure.persistence;

import com.example.orderapi.domain.model.EventFailed;
import com.example.orderapi.domain.repository.EventFailedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EventFailedRepositoryImpl implements EventFailedRepository {

    private final EventFailedJpaRepository repository;


    @Override
    public EventFailed save(EventFailed eventFailed) {
        return repository.save(eventFailed);
    }
}
