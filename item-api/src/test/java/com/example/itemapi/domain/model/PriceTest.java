package com.example.itemapi.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PriceTest {

    @Test
    void 비교할_가격이_음수이면_true() {
        Price price = new Price(BigDecimal.valueOf(-1));
        assertThat(price.negativeCheck()).isTrue();
    }

    @Test
    void 비교할_가격이_0이면_false() {
        Price price = new Price(BigDecimal.valueOf(0));
        assertThat(price.negativeCheck()).isFalse();
    }

    @Test
    void 비교할_가격이_양수이면_false() {
        Price price = new Price(BigDecimal.valueOf(1));
        assertThat(price.negativeCheck()).isFalse();
    }
}
