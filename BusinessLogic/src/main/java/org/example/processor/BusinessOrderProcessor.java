package org.example.processor;

import java.util.List;

import org.example.model.Order;
import org.example.model.SessionizeOrder;

public class BusinessOrderProcessor implements OrderProcessor<SessionizeOrder> {

    private final List<OrderProcessor<Order>> processors;

    private final OrderProcessor<SessionizeOrder> sessionProcessor;

    public BusinessOrderProcessor(
            List<OrderProcessor<Order>> processors,
            OrderProcessor<SessionizeOrder> sessionProcessor) {
        this.processors = processors;
        this.sessionProcessor = sessionProcessor;
    }

    @Override
    public SessionizeOrder process(Order order) {

        Order tmpOrder = order;
        for (OrderProcessor<? extends Order> processor : processors) {
            tmpOrder = processor.process(tmpOrder);
        }

        return sessionProcessor.process(tmpOrder);
    }
}
