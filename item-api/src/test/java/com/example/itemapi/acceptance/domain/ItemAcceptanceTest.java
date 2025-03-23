package com.example.itemapi.acceptance.domain;

import com.example.itemapi.interfaces.presentation.request.ItemRequest;
import groovy.util.logging.Slf4j;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.example.itemapi.acceptance.domain.ItemSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
public class ItemAcceptanceTest extends AcceptanceTest {

    @Test
    void 동시에_재고_차감_2개() {
        ItemRequest.AddItem request = ItemRequest.AddItem.builder()
                .name("모자")
                .price(BigDecimal.valueOf(10_000))
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
                .price(BigDecimal.valueOf(10_000))
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

    @Test
    void 재고_차감_웨이브_테스트_5번_웨이브_1000명_동시_요청() throws InterruptedException {
        ItemRequest.AddItem request = ItemRequest.AddItem.builder()
                .name("모자")
                .price(BigDecimal.valueOf(10_000))
                .stock(10_000)
                .build();
        ExtractableResponse<Response> addResponse = 제품_등록_요청(request);

        int waveCount = 5;
        int concurrencyPerWave = 1_000;
        decreaseTestWave((no) -> 재고_차감_요청(1L, 1), waveCount, concurrencyPerWave);
    }

    private void decreaseTestWave(Consumer<Void> action, int waveCount, int concurrencyPerWave) throws InterruptedException {
        int stock = 제품_단건_조회_요청(1L).jsonPath().getInt("stock");

        ExecutorService executorService = Executors.newFixedThreadPool(32);

        for (int wave = 1; wave <= waveCount; wave++) {
            CountDownLatch latch = new CountDownLatch(concurrencyPerWave);

            for (int i = 0; i < concurrencyPerWave; i++) {
                executorService.submit(() -> {
                    try {
                        action.accept(null);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            long sleepTime = (long) (500 + Math.random() * 1000);
            Thread.sleep(sleepTime);
        }

        executorService.shutdown();
        int resultStock = 제품_단건_조회_요청(1L).jsonPath().getInt("stock");

        long totalOrders = (long) waveCount * concurrencyPerWave;

        long expected = stock - totalOrders;
        long actual = resultStock;

        assertThat(expected).isEqualTo(actual);
    }
}
