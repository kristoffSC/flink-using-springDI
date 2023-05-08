package org.example.config;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.example.internal.CheckpointCountingSource.EventProducer;
import org.example.internal.ConsoleSink;
import org.example.internal.ConsoleSink.EventToStringConverter;
import org.example.internal.FlinkBusinessLogic;
import org.example.internal.OrderProducer;
import org.example.internal.SuspiciousFlinkBusinessLogic;
import org.example.model.Order;
import org.example.model.SessionizeOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlinkPipelineConfig {

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

    // Will be created only if "business.logic" system property is set to "standard" or property
    // is missing.
    @Bean
    @ConditionalOnProperty(
        value="business.logic",
        havingValue = "standard",
        matchIfMissing = true)
    public ProcessFunction<Order, SessionizeOrder> businessLogic() {
        return new FlinkBusinessLogic();
    }

    // Will be created only if "business.logic" system property is set to "sleep".
    @Bean
    @ConditionalOnProperty(
        value="business.logic",
        havingValue = "sleep")
    public ProcessFunction<Order, SessionizeOrder> suspiciousLogic() {
        return new SuspiciousFlinkBusinessLogic();
    }
}
