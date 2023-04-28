package org.example.internal;

import java.io.Serializable;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleSink<IN> extends RichSinkFunction<IN> {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleSink.class);

    private final EventToStringConverter<IN> converter;

    private transient int recordCount = 0;

    public ConsoleSink(EventToStringConverter<IN> converter) {
        Preconditions.checkNotNull(converter);
        this.converter = converter;
    }

    @Override
    public void invoke(IN row, Context context) throws Exception {
        String convert = converter.convert(row);
        LOG.info("Event content: " + convert);
        recordCount++;
    }

    @Override
    public void close() throws Exception {
        super.close();
        LOG.info("Total number of records (not checkpointed): " + recordCount);
    }

    public interface EventToStringConverter<IN> extends Serializable {
        String convert(IN event);
    }
}
