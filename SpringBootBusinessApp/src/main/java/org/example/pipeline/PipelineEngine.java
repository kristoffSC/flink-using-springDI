package org.example.pipeline;

import org.example.model.SessionizeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.example.model.Order;
import org.example.processor.OrderProcessor;

@Component
public class PipelineEngine {

    private final MessagingSystem messagingsystem;

    private final OrderProcessor<SessionizeOrder> processor;

    @Autowired
    public PipelineEngine(
            MessagingSystem messagingsystem,
            @Qualifier("businessOrderProcessor") OrderProcessor<SessionizeOrder> processor) {
        this.messagingsystem = messagingsystem;
        this.processor = processor;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 1500)
    public void scheduleFixedDelayTask() {
        Order newOrder = messagingsystem.poll();

        Order process = processor.process(newOrder);
        System.out.println("Pipeline produced - " + process);
    }
}
