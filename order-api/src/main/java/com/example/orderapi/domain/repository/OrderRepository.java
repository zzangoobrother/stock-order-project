package com.example.orderapi.domain.repository;

import com.example.orderapi.domain.model.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findBy(Long orderId);
}
