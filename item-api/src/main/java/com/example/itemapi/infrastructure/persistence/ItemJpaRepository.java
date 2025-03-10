package com.example.itemapi.infrastructure.persistence;

import com.example.itemapi.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
}
