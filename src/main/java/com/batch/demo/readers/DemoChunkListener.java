package com.batch.demo.readers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * Custom item read listener to show where we can put hooks in..
 */
public class DemoChunkListener implements ChunkListener {

    private static final Logger log = LoggerFactory.getLogger(DemoChunkListener.class);

    @Override
    public void beforeChunk(ChunkContext context) {
        // can put custom logic in here (this will flood the logs)
        log.info("*** ChunkListener: beforeChunk");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        // can put custom logic in here (this will flood the logs)
        log.info("*** ChunkListener: afterChunk");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        // can put custom logic in here (this will flood the logs)
        log.info("*** ChunkListener: chunkError" + context.getStepContext().getStepExecution().getSummary());
    }
}
