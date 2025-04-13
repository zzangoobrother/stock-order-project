package com.example.itemapi.interfaces.presentation.feign;

import com.example.itemapi.global.exception.ItemFeignClientErrorDecoder;
import com.example.itemapi.interfaces.presentation.response.ItemInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "itemClient", url = "http://localhost:8080/api/v1", configuration = ItemFeignClientErrorDecoder.class)
public interface ItemClient {

    @GetMapping(value = "/items/{itemId}", consumes = "application/json; charset=UTF-8")
    ItemInfoResponse getBy(@PathVariable Long itemId);
}
