package org.example.internal;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext;
import org.example.internal.CheckpointCountingSource.EventProducer;
import org.example.model.Order;

public class OrderProducer implements EventProducer<Order> {

    @Override
    public int emitRecordsBatch(int nextValue, SourceContext<Order> ctx, int batchSize) {

        while (batchSize-- > 0) {
            Order order = new Order(++nextValue, "SideA_" + nextValue, "SideB_" + nextValue);
            ctx.collect(order);
        }
        return nextValue;
    }

    @Override
    public TypeInformation<Order> getProducedType() {
        return TypeInformation.of(Order.class);
    }
}
