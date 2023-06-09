package org.example;

import java.util.List;
import org.example.model.SessionizeOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.example.model.Order;
import org.example.processor.BusinessOrderProcessor;
import org.example.processor.OrderProcessor;
import org.example.processor.OrderSessionize;
import org.example.processor.SideNameAnonymization;
import org.example.session.SessionManager;
import org.example.session.SimpleSessionManager;

@Configuration
@EnableScheduling
@EnableAutoConfiguration
public class SpringConfig {

    @Bean
    public SessionManager createSessionManager() {
        return new SimpleSessionManager();
    }

    @Bean
    public OrderSessionize orderSessionize(SessionManager sessionManager) {
        return new OrderSessionize(sessionManager);
    }

    @Bean
    public List<OrderProcessor<Order>> orderProcessors() {
        return List.of(new SideNameAnonymization());
    }

    @Bean("businessOrderProcessor")
    public OrderProcessor<SessionizeOrder> createOrderProcessor(
            List<OrderProcessor<Order>> orderProcessors,
            OrderSessionize orderSessionize) {
        return new BusinessOrderProcessor(orderProcessors, orderSessionize);
    }
}
