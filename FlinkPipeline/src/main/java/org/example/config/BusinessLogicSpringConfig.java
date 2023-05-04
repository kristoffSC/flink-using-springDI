package org.example.config;

import java.util.List;
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
public class BusinessLogicSpringConfig {

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
