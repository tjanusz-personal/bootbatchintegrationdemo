package com.batch.demo.readers;

import com.batch.demo.domain.DemoInputRow;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * Basic FieldSetMapper class responsible for mapping the various parts of the CSV file to our processing object.
 * We really needed this class because of the multiple identifiers in a field requirement.
 */
public class DemoInputFieldSetMapper implements FieldSetMapper<DemoInputRow> {

    @Override
    public DemoInputRow mapFieldSet(FieldSet fieldSet) throws BindException {
        DemoInputRow row = new DemoInputRow();

        // "recordId", "guid", "phones", "emails", "devices", "asOf"
        row.setRecordId(fieldSet.readString("recordId"));
        row.setGuid(fieldSet.readString("guid"));

        // phones and emails and devices can have multiple values within a field delimited by hashes
        // This is where we can add this logic
        row.setPhones(fieldSet.readString("phones"));
        row.setEmails(fieldSet.readString("emails"));
        row.setDevices(fieldSet.readString("devices"));
        row.setAsOf(fieldSet.readString("asOf"));

        return row;
    }

}
