package com.example.orderapi.interfaces.presentation.feign;

import com.example.orderapi.application.service.dto.request.PaymentCancelRequest;
import com.example.orderapi.application.service.dto.request.PaymentRequest;
import com.example.orderapi.application.service.dto.response.PaymentResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "paymentClient", url = "http://localhost:8082/api/v1")
public interface PaymentClient {

    @PostMapping(value = "/payments", consumes = "application/json; charset=UTF-8")
    PaymentResultResponse payment(@RequestBody PaymentRequest paymentRequest);

    @PostMapping(value = "/payments/cancel", consumes = "application/json; charset=UTF-8")
    PaymentResultResponse cancel(@RequestBody PaymentCancelRequest paymentRequest);
}
