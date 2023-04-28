package org.example.processor;

import org.example.model.Order;

public interface OrderProcessor<T extends Order> {

    T process(Order order);

}
