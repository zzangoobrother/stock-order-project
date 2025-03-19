package com.example.orderapi.domain.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED("주문 생성"),
    PAYMENT("결제 완료"),
    COMPLETED("주문 완료"),
    ;

    private String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
