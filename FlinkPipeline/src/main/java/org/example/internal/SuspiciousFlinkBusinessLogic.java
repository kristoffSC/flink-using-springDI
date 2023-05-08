package org.example.internal;

import com.getindata.fink.spring.context.ContextRegistry;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.example.model.Order;
import org.example.model.SessionizeOrder;
import org.example.processor.OrderProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SuspiciousFlinkBusinessLogic extends ProcessFunction<Order, SessionizeOrder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuspiciousFlinkBusinessLogic.class);

    @Autowired
    @Qualifier("businessOrderProcessor")
    private transient OrderProcessor<SessionizeOrder> orderProcessor;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        LOGGER.info("Using logic - SuspiciousFlinkBusinessLogic.");
        System.getProperties().putAll(
            getRuntimeContext().getExecutionConfig().getGlobalJobParameters().toMap()
        );
        new ContextRegistry().autowiredBean(this, "org.example.config");
    }

    @Override
    public void processElement(Order value, Context ctx, Collector<SessionizeOrder> out) throws Exception {
        SessionizeOrder newOrder = orderProcessor.process(value);
        suspiciousSleep();
        out.collect(newOrder);
    }

    private void suspiciousSleep() {
        try {
            int counter = 100;
            while (counter-- > 0) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
