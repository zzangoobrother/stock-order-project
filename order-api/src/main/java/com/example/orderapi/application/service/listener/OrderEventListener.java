package com.example.orderapi.application.service.listener;

import com.example.orderapi.application.service.OrderService;
import com.example.orderapi.application.service.listener.dto.OrderPaymentCompleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderEventListener {

    private final OrderService orderService;

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentComplete(OrderPaymentCompleteEvent event) {
        log.info("결제 완료 처리");
        orderService.paymentComplete(event.orderId(), event.itemId(), event.decreaseCount());
        log.info("모두 완료");
    }
}
