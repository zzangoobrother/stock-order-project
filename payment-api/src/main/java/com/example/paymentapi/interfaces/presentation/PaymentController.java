package com.example.paymentapi.interfaces.presentation;

import com.example.paymentapi.interfaces.presentation.request.PaymentRequest;
import com.example.paymentapi.interfaces.presentation.response.PaymentResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class PaymentController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/payments")
    public PaymentResultResponse payment(@RequestBody PaymentRequest request) {
        log.info("payment type : {}, price : {}", request.paymentType(), request.price());
        return new PaymentResultResponse("200", "결제 완료");
    }
}
