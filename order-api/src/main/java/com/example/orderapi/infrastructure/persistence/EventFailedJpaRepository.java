package com.example.orderapi.infrastructure.persistence;

import com.example.orderapi.domain.model.EventFailed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventFailedJpaRepository extends JpaRepository<EventFailed, Long> {
}

