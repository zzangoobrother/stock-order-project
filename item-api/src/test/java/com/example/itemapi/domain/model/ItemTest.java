package com.example.itemapi.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ItemTest {

	private Item item;

	@BeforeEach
	void setup() {
		item = Item.builder()
			.name("모자")
			.price(BigDecimal.valueOf(10_000))
			.stock(2)
			.build();
	}

	@ParameterizedTest
	@CsvSource(value = {"2:0", "1:1"}, delimiter = ':')
	void 재고_감소_성공(int decreaseCount, int result) {
		item.decreaseStock(decreaseCount);

		assertThat(item.getStock()).isEqualTo(result);
	}

	@ValueSource(ints = {0, -1})
	@ParameterizedTest
	void 재고_감소_입력값이_0_이하이면_에러(int value) {
		assertThatThrownBy(() -> item.decreaseStock(value))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("재고 감소 수량은 1 이상이어야 합니다.");
	}

	@Test
	void 재고_감소_입력값이_재고보다_크면_에러() {
		assertThatThrownBy(() -> item.decreaseStock(3))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("재고가 부족합니다.");
	}
}
