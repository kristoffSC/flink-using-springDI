package org.example.internal;

import java.io.Serializable;
import java.util.Collections;
import org.apache.flink.api.common.state.CheckpointListener;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Each of the source operators outputs records in given number of checkpoints. Number of records
 * per checkpoint is constant between checkpoints, and defined by user. When all records are
 * emitted, the source waits for two more checkpoints until it finishes.
 * <p>
 * All credits for this implementation goes to <b>Grzegorz Kolakowski<b> who implemented the
 * original version in one of our open-source projects.
 */
public class CheckpointCountingSource<OUT> extends RichParallelSourceFunction<OUT>
    implements CheckpointListener, CheckpointedFunction, ResultTypeQueryable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckpointCountingSource.class);

    private final int numberOfCheckpoints;

    private final int recordsPerCheckpoint;

    private final EventProducer<OUT> eventProducer;

    private ListState<Integer> nextValueState;

    private int nextValue;

    private volatile boolean isCanceled;

    private volatile boolean waitingForCheckpoint;

    public CheckpointCountingSource(
            int recordsPerCheckpoint,
            int numberOfCheckpoints,
            EventProducer<OUT> eventProducer) {

        this.numberOfCheckpoints = numberOfCheckpoints;
        this.recordsPerCheckpoint = recordsPerCheckpoint;
        this.eventProducer = eventProducer;
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        nextValueState = context.getOperatorStateStore()
            .getListState(new ListStateDescriptor<>("nextValue", Integer.class));

        if (nextValueState.get() != null && nextValueState.get().iterator().hasNext()) {
            nextValue = nextValueState.get().iterator().next();
        }
        waitingForCheckpoint = false;
    }

    @Override
    public void run(SourceContext<OUT> ctx) throws Exception {
        LOGGER.debug("Run subtask={}; attempt={}.",
            getRuntimeContext().getIndexOfThisSubtask(),
            getRuntimeContext().getAttemptNumber());

        sendRecordsUntil(numberOfCheckpoints, ctx);
        idleUntilNextCheckpoint(ctx);
        LOGGER.debug("Source task done; subtask={}.",
            getRuntimeContext().getIndexOfThisSubtask());
    }

    private void sendRecordsUntil(int targetCheckpoints, SourceContext<OUT> ctx)
        throws InterruptedException {
        while (!isCanceled && nextValue < targetCheckpoints * recordsPerCheckpoint) {
            synchronized (ctx.getCheckpointLock()) {
                emitRecordsBatch(recordsPerCheckpoint, ctx);
                waitingForCheckpoint = true;
            }
            LOGGER.debug("Waiting for checkpoint to complete; subtask={}.",
               getRuntimeContext().getIndexOfThisSubtask());
            while (waitingForCheckpoint) {
                Thread.sleep(1);
            }
        }
    }

    private void emitRecordsBatch(int batchSize, SourceContext<OUT> ctx) {
        nextValue = eventProducer.emitRecordsBatch(nextValue, ctx, batchSize);
        LOGGER.debug("Emitted {} records (total {}); subtask={}.", batchSize, nextValue,
            getRuntimeContext().getIndexOfThisSubtask());
    }

    private void idleUntilNextCheckpoint(SourceContext<OUT> ctx) throws
        InterruptedException {
        // Idle until the next checkpoint completes to avoid any premature job termination and
        // race conditions.
        LOGGER.info("Waiting for an additional checkpoint to complete; subtask={}.",
            getRuntimeContext().getIndexOfThisSubtask());
        synchronized (ctx.getCheckpointLock()) {
            waitingForCheckpoint = true;
        }
        while (waitingForCheckpoint) {
            Thread.sleep(1L);
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        nextValueState.update(Collections.singletonList(nextValue));
        LOGGER.debug("state snapshot done; checkpointId={}; subtask={}.",
            context.getCheckpointId(),
            getRuntimeContext().getIndexOfThisSubtask());
    }

    @Override
    public void notifyCheckpointComplete(long checkpointId) {
        waitingForCheckpoint = false;
        LOGGER.debug("Checkpoint {} complete; subtask={}.", checkpointId,
            getRuntimeContext().getIndexOfThisSubtask());
    }

    @Override
    public void notifyCheckpointAborted(long checkpointId) throws Exception {
        LOGGER.info("Checkpoint {} aborted; subtask={}.", checkpointId,
            getRuntimeContext().getIndexOfThisSubtask());
        CheckpointListener.super.notifyCheckpointAborted(checkpointId);
    }

    @Override
    public void cancel() {
        isCanceled = true;
    }

    @Override
    public TypeInformation<OUT> getProducedType() {
        return eventProducer.getProducedType();
    }

    public interface EventProducer<OUT> extends Serializable {

        int emitRecordsBatch(int nextValue, SourceContext<OUT> ctx, int batchSize);

        TypeInformation<OUT> getProducedType();
    }
}
