package com.example.itemapi.infrastructure.persistence;

import com.example.itemapi.domain.model.Item;
import com.example.itemapi.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository repository;

    @Override
    public Item save(Item item) {
        return repository.save(item);
    }
}
