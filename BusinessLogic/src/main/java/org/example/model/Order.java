package org.example.model;

public class Order {

    private final int orderId;

    private final String orderSideA;

    private final String orderSideB;

    public Order(int orderId, String orderSideA, String orderSideB) {
        this.orderId = orderId;
        this.orderSideA = orderSideA;
        this.orderSideB = orderSideB;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderSideA() {
        return orderSideA;
    }

    public String getOrderSideB() {
        return orderSideB;
    }

    @Override
    public String toString() {
        return "Order{" +
            "orderId=" + orderId +
            ", orderSideA='" + orderSideA + '\'' +
            ", orderSideB='" + orderSideB + '\'' +
            '}';
    }
}
