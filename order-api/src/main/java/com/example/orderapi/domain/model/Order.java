package com.example.orderapi.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus orderStatus;

    @Builder
    public Order(Long itemId, int quantity, OrderStatus orderStatus) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
    }

    public void paymentResult() {
        this.orderStatus = OrderStatus.PAYMENT;
    }

    public void complete() {
        this.orderStatus = OrderStatus.COMPLETED;
    }
}
