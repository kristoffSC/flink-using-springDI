package org.example.pipeline;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

import org.example.model.Order;

@Component
public class MessagingSystem {

    private final ArrayBlockingQueue<Order> messageQueue;

    public MessagingSystem() {
        this.messageQueue = new ArrayBlockingQueue<>(100, true);
    }

    public void send(Order order) {
        try {
            this.messageQueue.put(order);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Order poll() {
        try {
            return this.messageQueue.poll(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
