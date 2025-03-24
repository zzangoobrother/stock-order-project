package com.example.itemapi.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

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
    private Price price;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "is_delete", nullable = false)
    private boolean isDelete;

    @Builder
    public Item(String name, BigDecimal price, int stock, boolean isDelete) {
        this(name, new Price(price), stock, isDelete);
    }

    private Item(String name, Price price, int stock, boolean isDelete) {
        validated(name, stock);

        this.name = name;
        this.price = price;
        this.stock = stock;
        this.isDelete = isDelete;
    }

    private void validated(String name, int stock) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("제품명을 입력해주세요.");
        }

        if (stock < 0) {
            throw new IllegalArgumentException("재고를 0 이상 입력해주세요.");
        }
    }

    public String getPriceToString() {
        return String.valueOf(this.price.getValue().intValue());
    }

    public void decreaseStock(int decreaseCount) {
        if (decreaseCount <= 0) {
            throw new IllegalArgumentException("재고 감소 수량은 1 이상이어야 합니다.");
        }

        if (this.stock < decreaseCount) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        this.stock -= decreaseCount;
    }

    public void delete() {
        this.isDelete = true;
    }
}
