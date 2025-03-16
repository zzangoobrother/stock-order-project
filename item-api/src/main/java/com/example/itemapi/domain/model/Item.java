package com.example.itemapi.domain.model;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Embedded
    @AttributeOverride(name = "price", column = @Column(name = "price", nullable = false))
    private Price price;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Builder
    public Item(String name, BigDecimal price, int stock) {
        this(name, new Price(price), stock);
    }

    private Item(String name, Price price, int stock) {
        validated(name, stock);

        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    private void validated(String name, int stock) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("제품명을 입력해주세요.");
        }

        if (stock < 0) {
            throw new IllegalArgumentException("재고를 0 이상 입력해주세요.");
        }
    }
}
