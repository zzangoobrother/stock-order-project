package com.example.itemapi.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.example.itemapi.domain.model.Item;
import com.example.itemapi.domain.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository repository;

    @Override
    public Item save(Item item) {
        return repository.save(item);
    }

    @Override
    public boolean existsBy(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Optional<Item> findByIdAndIsDeleteFalse(Long itemId) {
        return repository.findByIdAndIsDeleteFalse(itemId);
    }
}
