package com.example.itemapi.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Test
    void 비교할_가격이_음수이면_true() {
        assertThatThrownBy(() -> new Price(BigDecimal.valueOf(-1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 음수가 입력될 수 없습니다.");
    }

    @Test
    void 비교할_가격이_0이면_false() {
        Price price = new Price(BigDecimal.valueOf(0));
        assertThat(price.getValue()).isEqualTo(BigDecimal.valueOf(0));
    }

    @Test
    void 비교할_가격이_양수이면_false() {
        Price price = new Price(BigDecimal.valueOf(1));
        assertThat(price.getValue()).isEqualTo(BigDecimal.valueOf(1));
    }
}
