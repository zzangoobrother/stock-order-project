package com.example.orderapi.interfaces.presentation;

import com.example.orderapi.application.service.OrderService;
import com.example.orderapi.interfaces.presentation.feign.QueueClient;
import com.example.orderapi.interfaces.presentation.request.OrderRequest;
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
public class OrderController {

    private final OrderService orderService;
    private final QueueClient queueClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/orders")
    public ResponseEntity createOrder(@RequestBody OrderRequest request) {
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

        orderService.createOrder(request.itemId(), request.quantity());
        return ResponseEntity.ok().build();
    }
}
