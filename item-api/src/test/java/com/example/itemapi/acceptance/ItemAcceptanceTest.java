package com.example.itemapi.acceptance;

import com.example.itemapi.global.redis.RedisPublisher;
import com.example.itemapi.interfaces.presentation.feign.ItemClient;
import com.example.itemapi.interfaces.presentation.feign.QueueClient;
import com.example.itemapi.interfaces.presentation.request.ItemRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static com.example.itemapi.acceptance.ItemSteps.제품_단건_조회_요청;
import static com.example.itemapi.acceptance.ItemSteps.제품_등록_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("제품 관련 기능")
public class ItemAcceptanceTest extends AcceptanceTest {

	@MockitoBean
	private ItemClient itemClient;

	@MockitoBean
	private RedisPublisher redisPublisher;

	@MockitoBean
	private QueueClient queueClient;

	@Test
	void 제품_등록을_한다() {
		when(queueClient.getBy(anyString())).thenReturn(true);

		ItemRequest.AddItem request = ItemRequest.AddItem.builder()
			.name("모자")
			.price(BigDecimal.valueOf(1_000))
			.stock(10)
			.build();

		ExtractableResponse<Response> response = 제품_등록_요청(request);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@Test
	void 제품을_조회_한다() {
		when(queueClient.getBy(anyString())).thenReturn(true);

		ItemRequest.AddItem request = ItemRequest.AddItem.builder()
				.name("모자")
				.price(BigDecimal.valueOf(1_000))
				.stock(10)
				.build();

		ExtractableResponse<Response> createResponse = 제품_등록_요청(request);

		ExtractableResponse<Response> response = 제품_단건_조회_요청(createResponse);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}
}
