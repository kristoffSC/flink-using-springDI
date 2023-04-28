package org.example.model;

public class SessionizeOrder extends Order {

    private final String sessionId;

    public SessionizeOrder(String sessionId, int orderId, String orderSideA, String orderSideB) {
        super(orderId, orderSideA, orderSideB);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return "SessionizeOrder{" +
            "sessionId='" + sessionId + '\'' +
            "} " + super.toString();
    }
}
