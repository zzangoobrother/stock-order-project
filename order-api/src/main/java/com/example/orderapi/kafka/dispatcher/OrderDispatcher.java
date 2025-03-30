package com.example.orderapi.kafka.dispatcher;

public interface OrderDispatcher {

    boolean supports(Object event);

    void execute(Object event);
}
