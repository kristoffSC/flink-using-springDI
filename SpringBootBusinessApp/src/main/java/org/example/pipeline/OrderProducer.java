package org.example.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.example.model.Order;

@Component
public class OrderProducer {

    private final MessagingSystem messagingsystem;

    private int orderId;

    @Autowired
    public OrderProducer(MessagingSystem messagingsystem) {
        this.messagingsystem = messagingsystem;
        this.orderId = 0;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void scheduleFixedDelayTask() {
        messagingsystem.send(
            new Order(++orderId, "sideA_" + orderId, "sideB_" + orderId)
        );
    }
}
