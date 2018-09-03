package com.batch.demo.readers;

import com.batch.demo.domain.DemoInputRow;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DemoInputFieldSetMapperTest {

    private DemoInputFieldSetMapper mapper;

    @Before
    public void setUp() {
        this.mapper = new DemoInputFieldSetMapper();
    }

    @Test
    public void mapFieldSetMapsAllStringValuesCorrectly() throws BindException {
        String[] fieldNames = { "recordId", "guid", "phones", "emails", "devices", "asOf"};
        String[] fieldValues = { "100", "aaa-bb", "phone_1", "email_1", "device_1", "as_of"};
        FieldSet fieldSet = new DefaultFieldSet(fieldValues, fieldNames);
        DemoInputRow inputRow = mapper.mapFieldSet(fieldSet);
        assertThat("100", is(inputRow.getRecordId()));
        assertThat("aaa-bb", is(inputRow.getGuid()));
        assertThat("phone_1", is(inputRow.getPhones()));
        assertThat("email_1", is(inputRow.getEmails()));
        assertThat("device_1", is(inputRow.getDevices()));
        assertThat("as_of", is(inputRow.getAsOf()));
    }

    @Test
    public void mapFieldSetMapsAnyMissingFieldsCorrectly() throws BindException {
        String[] fieldNames = { "recordId", "guid", "phones", "emails", "devices", "asOf"};
        String[] fieldValues = { "100", "aaa-bb", "phone_1", "email_1", "", ""};
        FieldSet fieldSet = new DefaultFieldSet(fieldValues, fieldNames);
        DemoInputRow inputRow = mapper.mapFieldSet(fieldSet);
        assertThat("100", is(inputRow.getRecordId()));
        assertThat("aaa-bb", is(inputRow.getGuid()));
        assertThat("phone_1", is(inputRow.getPhones()));
        assertThat("email_1", is(inputRow.getEmails()));
        assertThat("", is(inputRow.getDevices()));
        assertThat("", is(inputRow.getAsOf()));
    }

}