package com.example.itemapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itemapi.domain.model.EventFailed;

public interface EventFailedJpaRepository extends JpaRepository<EventFailed, Long> {
}
