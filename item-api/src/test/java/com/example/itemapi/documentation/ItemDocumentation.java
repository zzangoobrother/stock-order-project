package com.example.itemapi.documentation;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.math.BigDecimal;

import com.example.itemapi.global.redis.RedisPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.itemapi.application.service.ItemService;
import com.example.itemapi.interfaces.presentation.feign.QueueClient;
import com.example.itemapi.interfaces.presentation.request.ItemRequest;

import io.restassured.RestAssured;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;

public class ItemDocumentation extends Documentation {

	@MockitoBean
	private ItemService itemService;

	@MockitoBean
	private QueueClient queueClient;

	@Test
	void addItem() {
		ItemRequest.AddItem request = ItemRequest.AddItem.builder()
			.name("모자")
			.price(BigDecimal.ZERO)
			.stock(10)
			.build();

		RestAssured
			.given(spec).log().all()
			.filter(document("/items",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("name").description("제품명"),
					fieldWithPath("price").description("가격"),
					fieldWithPath("stock").description("수량")
				)))
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(request)
			.when().post("/api/v1/items")
			.then().log().all().extract();
	}
}
