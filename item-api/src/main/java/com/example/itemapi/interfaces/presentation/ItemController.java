package com.example.itemapi.interfaces.presentation;

import com.example.itemapi.application.service.ItemService;
import com.example.itemapi.interfaces.presentation.request.ItemRequest;
import com.example.itemapi.interfaces.presentation.response.ItemInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class ItemController {

    public final ItemService itemService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    public void addItem(@RequestBody ItemRequest.AddItem request) {
        itemService.addItem(request.name().trim(), request.price(), request.stock());
    }

    @GetMapping("/items/{itemId}")
    public ItemInfoResponse getBy(@PathVariable(name = "itemId") Long itemId) {
        return ItemInfoResponse.toItemInfoResponse(itemService.getBy(itemId));
    }
}
