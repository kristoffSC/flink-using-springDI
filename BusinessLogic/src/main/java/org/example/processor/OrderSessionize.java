package org.example.processor;

import org.example.model.Order;
import org.example.model.SessionizeOrder;
import org.example.session.SessionManager;

public class OrderSessionize implements OrderProcessor<SessionizeOrder> {

    private final SessionManager sessionManager;

    public OrderSessionize(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public SessionizeOrder process(Order order) {
        String sessionId = sessionManager.getSessionId(order.getOrderId());
        return new SessionizeOrder(
            sessionId,
            order.getOrderId(),
            order.getOrderSideA(),
            order.getOrderSideB()
        );
    }
}
