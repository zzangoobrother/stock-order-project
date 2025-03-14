package com.example.itemapi.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Price {
    private BigDecimal value;

    public Price(BigDecimal value) {
        if (negativeCheck()) {
            throw new IllegalArgumentException("가격은 음수가 입력될 수 없습니다.");
        }
        
        this.value = value;
    }

    public boolean negativeCheck() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
}
