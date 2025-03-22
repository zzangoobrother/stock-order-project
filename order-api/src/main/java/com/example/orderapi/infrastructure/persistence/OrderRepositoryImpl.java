package com.example.orderapi.infrastructure.persistence;

import com.example.orderapi.domain.model.Order;
import com.example.orderapi.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository repository;

    @Override
    public Order save(Order order) {
        return repository.save(order);
    }

    @Override
    public Optional<Order> getBy(Long orderId) {
        return repository.findById(orderId);
    }
}
