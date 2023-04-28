package org.example.processor;

import java.util.UUID;

import org.example.model.Order;

public class SideNameAnonymization implements OrderProcessor<Order> {

    private String getRandomString() {
        return UUID.randomUUID().toString().split("-")[0];
    }

    @Override
    public Order process(Order order) {
        return new Order(
            order.getOrderId(),
            getRandomString(),
            getRandomString()
        );
    }
}
