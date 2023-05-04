package org.example.internal;

import com.getindata.fink.spring.context.ContextRegistry;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.example.model.Order;
import org.example.model.SessionizeOrder;
import org.example.processor.OrderProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class FlinkBusinessLogic extends ProcessFunction<Order, SessionizeOrder> {

    @Autowired
    @Qualifier("businessOrderProcessor")
    private OrderProcessor<SessionizeOrder> orderProcessor;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        new ContextRegistry().autowiredBean(this, "org.example.config");
    }

    @Override
    public void processElement(Order value, Context ctx, Collector<SessionizeOrder> out) throws Exception {
        SessionizeOrder newOrder = orderProcessor.process(value);
        out.collect(newOrder);
    }
}
