package com.example.orderapi.interfaces.presentation.feign;

import com.example.orderapi.global.exception.QueueFeignClientErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "queueClient", url = "http://localhost:8083/api/v1/queue", configuration = QueueFeignClientErrorDecoder.class)
public interface QueueClient {

    @GetMapping(value = "/allowed", consumes = "application/json; charset=UTF-8")
    boolean getBy(@RequestParam("queue") String queue);
}
