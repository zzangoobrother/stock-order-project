package com.example.itemapi.domain.model;

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
        this.value = value;
    }

    public boolean negativeCheck() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
}
