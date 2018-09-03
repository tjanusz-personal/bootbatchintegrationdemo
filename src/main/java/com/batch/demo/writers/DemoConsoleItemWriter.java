package com.batch.demo.writers;


import com.batch.demo.domain.DemoInputRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.List;

/**
 * Simple console writing item writer to write out the input file contents.
 */
public class DemoConsoleItemWriter implements ItemWriter<DemoInputRow> {

    private static final Logger log = LoggerFactory.getLogger(DemoConsoleItemWriter.class);
    private boolean headerWritten = false;

    @Override
    public void write(List items) throws Exception {
        List<DemoInputRow> rows = items;

        // need this for multi threading to stop multiple headers being written
        synchronized (this) {
            if (!headerWritten) {
                log.info(this.header);
                headerWritten = true;
            }
        }

        rows.forEach(item -> {
            log.info(this.lineAggregator.aggregate(item));
        });
    }

    private String header;
    private LineAggregator<DemoInputRow> lineAggregator;

    public void setHeader(String header) {
        this.header = header;
    }

    public void setLineAggregator(LineAggregator<DemoInputRow> lineAggregator) {
        this.lineAggregator = lineAggregator;
    }
}
