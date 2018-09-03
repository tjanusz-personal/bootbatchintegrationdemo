package com.batch.demo.processors;

import com.batch.demo.domain.DemoInputRow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Scope;

@Scope("step")
public class DemoItemProcessor implements ItemProcessor<DemoInputRow, DemoInputRow> {

    @Override
    public DemoInputRow process(DemoInputRow item) throws Exception {
        // Do nothing right now but this is where each row is processed just return existing item for demo purposes
        return item;
    }
}
