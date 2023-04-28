package org.example.config;

import java.util.List;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.example.internal.CheckpointCountingSource.EventProducer;
import org.example.internal.ConsoleSink;
import org.example.internal.ConsoleSink.EventToStringConverter;
import org.example.internal.OrderProducer;
import org.example.model.Order;
import org.example.model.SessionizeOrder;
import org.example.processor.BusinessOrderProcessor;
import org.example.processor.OrderProcessor;
import org.example.processor.OrderSessionize;
import org.example.processor.SideNameAnonymization;
import org.example.session.SessionManager;
import org.example.session.SimpleSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobSpringConfig {

    @Bean
    public EventToStringConverter<SessionizeOrder> converter() {
        return event -> String.format("Order Details - %s", event.toString());
    }

    @Bean
    public SinkFunction<SessionizeOrder> sink(EventToStringConverter<SessionizeOrder> converter) {
        return new ConsoleSink<>(converter);
    }

    @Bean
    public EventProducer<Order> eventProducer() {
        return new OrderProducer();
    }

    @Bean
    public SessionManager sessionManager() {
        return new SimpleSessionManager();
    }

    @Bean
    public OrderProcessor<SessionizeOrder> orderProcessor(SessionManager sessionManager) {
        return new BusinessOrderProcessor(
            List.of(new SideNameAnonymization()),
            new OrderSessionize(sessionManager)
        );
    }
}
