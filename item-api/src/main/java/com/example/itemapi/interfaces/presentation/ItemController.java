package com.example.itemapi.interfaces.presentation;

import com.example.itemapi.application.service.ItemService;
import com.example.itemapi.interfaces.presentation.request.ItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ItemController {

    public final ItemService itemService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/items")
    public void addItem(@RequestBody ItemRequest.AddItem request) {
        itemService.addItem(request.name(), request.price(), request.stock());
    }
}
