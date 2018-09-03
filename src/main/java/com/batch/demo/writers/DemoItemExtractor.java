package com.batch.demo.writers;


import com.batch.demo.domain.DemoInputRow;
import org.springframework.batch.item.file.transform.FieldExtractor;

import java.util.ArrayList;
import java.util.List;

public class DemoItemExtractor implements FieldExtractor<DemoInputRow> {

    @Override
    public Object[] extract(DemoInputRow item) {
        List<Object> itemsToReturn = new ArrayList<Object>();
        itemsToReturn.add(item.getRecordId());
        itemsToReturn.add(item.getGuid());
        itemsToReturn.add(item.getPhones());
        itemsToReturn.add(item.getEmails());
        itemsToReturn.add(item.getDevices());
        itemsToReturn.add(item.getAsOf());
        return itemsToReturn.toArray();
    }
}
