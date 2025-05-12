package com.example.itemapi.interfaces.presentation;

import com.example.itemapi.application.service.ItemService;
import com.example.itemapi.interfaces.presentation.feign.QueueClient;
import com.example.itemapi.interfaces.presentation.request.ItemRequest;
import com.example.itemapi.interfaces.presentation.response.ItemInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
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
        boolean result = queueClient.getBy("order");
        if (!result) {
            try {
                URI redirectUri = new URI("http://localhost:8083/api/v1/queue");
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setLocation(redirectUri);
                return ResponseEntity.status(HttpStatus.SEE_OTHER).headers(httpHeaders).build();
            } catch (URISyntaxException e) {
                log.error("url 변환 오류");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        ItemInfoResponse itemInfoResponse = ItemInfoResponse.toItemInfoResponse(itemService.getBy(itemId));
        return ResponseEntity.ok().body(itemInfoResponse);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteBy(@PathVariable Long itemId) {
        itemService.deleteBy(itemId);
        return ResponseEntity.noContent().build();
    }
}
