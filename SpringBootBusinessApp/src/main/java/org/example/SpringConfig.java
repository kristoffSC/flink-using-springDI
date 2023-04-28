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
    public OrderProcessor<SessionizeOrder> createOrderProcessor(SessionManager sessionManager) {
        return new BusinessOrderProcessor(
            List.of(new SideNameAnonymization()),
            new OrderSessionize(sessionManager)
        );
    }
}
