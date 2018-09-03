package com.batch.demo.readers;

import com.batch.demo.domain.DemoInputRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;

/**
 * Custom item read listener to show where we can put hooks in..
 */
public class DemoItemReadListener implements ItemReadListener<DemoInputRow> {

    private static final Logger log = LoggerFactory.getLogger(DemoInputRow.class);

    @Override
    public void beforeRead() {
        // can put custom logic in here (this will flood the logs)
//        log.info("*** ItemReadListener: beforeRead");
    }

    @Override
    public void afterRead(DemoInputRow item) {
        // can put custom logic in here (this will flood the logs)
//        log.info("*** ItemReadListener: afterRead");
    }

    @Override
    public void onReadError(Exception ex) {
        log.info("*** ItemReadListener: onReadError");
    }
}
