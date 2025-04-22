package com.example.itemapi.interfaces.presentation;

import com.example.itemapi.application.service.ItemService;
import com.example.itemapi.interfaces.presentation.feign.QueueClient;
import com.example.itemapi.interfaces.presentation.request.ItemRequest;
import com.example.itemapi.interfaces.presentation.response.ItemInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class ItemController {

    private final ItemService itemService;
    private final QueueClient queueClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    public ResponseEntity addItem(@RequestBody ItemRequest.AddItem request) {
        itemService.addItem(request.name().trim(), request.price(), request.stock());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<ItemInfoResponse> getBy(@PathVariable(name = "itemId") Long itemId) {
        validateQueueProceed();
        ItemInfoResponse itemInfoResponse = ItemInfoResponse.toItemInfoResponse(itemService.getBy(itemId));
        return ResponseEntity.ok().body(itemInfoResponse);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteBy(@PathVariable Long itemId) {
        itemService.deleteBy(itemId);
        return ResponseEntity.noContent().build();
    }

    private void validateQueueProceed() {
        boolean result = queueClient.getBy("order");
        if (!result) {
            throw new IllegalArgumentException("토큰 확인 필요");
        }
    }
}
