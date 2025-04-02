package com.example.orderapi.domain.repository;

import com.example.orderapi.domain.model.EventFailed;

public interface EventFailedRepository {

    EventFailed save(EventFailed eventFailed);
}
