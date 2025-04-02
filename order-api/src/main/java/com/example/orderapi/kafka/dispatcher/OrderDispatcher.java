package com.example.orderapi.kafka.dispatcher;

public interface OrderDispatcher<T> {

    boolean supports(Object event);

    void execute(T event);
}
