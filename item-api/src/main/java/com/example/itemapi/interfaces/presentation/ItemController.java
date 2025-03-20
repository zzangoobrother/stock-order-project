package com.example.itemapi.interfaces.presentation;

import com.example.itemapi.application.service.ItemService;
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

    public final ItemService itemService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    public ResponseEntity addItem(@RequestBody ItemRequest.AddItem request) {
        itemService.addItem(request.name().trim(), request.price(), request.stock());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<ItemInfoResponse> getBy(@PathVariable(name = "itemId") Long itemId) {
        ItemInfoResponse itemInfoResponse = ItemInfoResponse.toItemInfoResponse(itemService.getBy(itemId));
        return ResponseEntity.ok().body(itemInfoResponse);
    }

    @PostMapping("/items/{itemId}/decrease-stock")
    public ResponseEntity<Void> decreaseStock(@PathVariable Long itemId, @RequestBody ItemRequest.DecreaseStock decreaseStock) {
        itemService.decreaseStock(itemId, decreaseStock.decreaseCount());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteBy(@PathVariable Long itemId) {
        itemService.deleteBy(itemId);
        return ResponseEntity.noContent().build();
    }
}
