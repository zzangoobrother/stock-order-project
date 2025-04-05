package com.example.orderapi.kafka.dispatcher;

public interface OrderDispatcher<T> {

    boolean supports(String event);

    void execute(T event);
}
