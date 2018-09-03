package com.batch.demo.processors;

import com.batch.demo.domain.DemoInputRow;
import org.springframework.batch.core.SkipListener;

/**
 * Simple skip listener to write out which recordId is not being processed.
 */
public class DemoSkipListener implements SkipListener {

    @Override
    public void onSkipInRead(Throwable t) {
        // do nothing..
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        // do nothing..
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        // This is where we can inject what we want to do with invalid items.
        DemoInputRow row = (DemoInputRow) item;
        StringBuilder builder = new StringBuilder();
        builder.append("DemoSkipListener - onSkipInWrite: SKIPPED ITEM #");
        builder.append(row.getRecordId());
        System.out.println(builder.toString());
    }
}
