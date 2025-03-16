package com.example.itemapi.domain.manager;

import com.example.itemapi.domain.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ItemManagerTest {

    @Autowired
    private ItemManager itemManager;

    @Test
    void 동시에_재고_차감() throws InterruptedException {
        itemManager.addItem("모자", BigDecimal.valueOf(10000), 3);

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> itemManager.decreaseStock(1L, 1)),
                CompletableFuture.runAsync(() -> itemManager.decreaseStock(1L, 1))
        ).join();

        Thread.sleep(100);

        Item item = itemManager.getBy(1L);
        assertThat(item.getStock()).isEqualTo(1);
    }
}
