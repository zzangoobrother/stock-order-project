package com.example.itemapi.infrastructure.persistence;

import com.example.itemapi.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    boolean existsByName(String name);

    Optional<Item> findByIdAndDeleteYnFalse(Long itemId);
}
