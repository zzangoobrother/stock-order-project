package com.example.orderapi.infrastructure.persistence;

import com.example.orderapi.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

}
