package com.example.orderapi.global.exception;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

public class PaymentFeignClientErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder decoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        if (HttpStatus.valueOf(response.status()).is5xxServerError()) {
            return new RetryableException(response.status(), response.reason(), response.request().httpMethod(), (Long) null, response.request());
        }

        return decoder.decode(s, response);
    }
}
