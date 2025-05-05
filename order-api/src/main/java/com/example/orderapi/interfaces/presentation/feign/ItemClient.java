package com.example.orderapi.interfaces.presentation.feign;

import com.example.orderapi.application.service.dto.request.DecreaseStockRequest;
import com.example.orderapi.application.service.dto.response.ItemInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "itemClient", url = "http://localhost:8084/api/v1")
public interface ItemClient {

    @GetMapping(value = "/items/{itemId}", consumes = "application/json; charset=UTF-8")
    ItemInfoResponse getBy(@PathVariable Long itemId);

    @PostMapping(value = "/items/{itemId}/decrease-stock", consumes = "application/json; charset=UTF-8")
    void decreaseStock(@PathVariable Long itemId, @RequestBody DecreaseStockRequest request);
}
