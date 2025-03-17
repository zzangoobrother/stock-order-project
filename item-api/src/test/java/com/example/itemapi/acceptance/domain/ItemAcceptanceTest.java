package com.example.itemapi.acceptance.domain;

import com.example.itemapi.interfaces.presentation.request.ItemRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static com.example.itemapi.acceptance.domain.ItemSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ItemAcceptanceTest extends AcceptanceTest {

    @Test
    void 동시에_재고_차감_2개() {
        ItemRequest.AddItem request = ItemRequest.AddItem.builder()
                .name("모자")
                .price(BigDecimal.valueOf(10000))
                .stock(2)
                .build();
        ExtractableResponse<Response> addResponse = 제품_등록_요청(request);

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1))
        ).join();

        ExtractableResponse<Response> response = 제품_단건_조회_요청(1L);

        assertAll(
                () -> assertThat(response.jsonPath().getLong("itemId")).isEqualTo(1L),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("모자"),
                () -> assertThat(response.jsonPath().getString("price")).isEqualTo("10000"),
                () -> assertThat(response.jsonPath().getInt("stock")).isEqualTo(0)
        );
    }

    @Test
    void 동시에_재고_차감_10개() throws InterruptedException {
        ItemRequest.AddItem request = ItemRequest.AddItem.builder()
                .name("모자")
                .price(BigDecimal.valueOf(10000))
                .stock(10)
                .build();
        ExtractableResponse<Response> addResponse = 제품_등록_요청(request);

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1)),
                CompletableFuture.runAsync(() -> 재고_차감_요청(1L, 1))
        ).join();

        ExtractableResponse<Response> response = 제품_단건_조회_요청(1L);

        assertAll(
                () -> assertThat(response.jsonPath().getLong("itemId")).isEqualTo(1L),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("모자"),
                () -> assertThat(response.jsonPath().getString("price")).isEqualTo("10000"),
                () -> assertThat(response.jsonPath().getInt("stock")).isEqualTo(0)
        );
    }
}
