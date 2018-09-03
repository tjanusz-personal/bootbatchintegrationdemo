package com.batch.demo.writers;

import com.batch.demo.domain.DemoInputRow;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DemoItemExtractorTest {

    private DemoItemExtractor fieldExtractor;

    @Before
    public void setUp() {
        fieldExtractor = new DemoItemExtractor();
    }

    @Test
    public void extractReturnsObjectArrayWithAllFieldsPopulated() {
        DemoInputRow dummyRow = new DemoInputRow();
        dummyRow.setGuid("aaa-bb");
        dummyRow.setAsOf("asof");
        dummyRow.setDevices("device_1");
        dummyRow.setEmails("email_1");
        dummyRow.setPhones("phones_1");
        dummyRow.setRecordId("100");

        Object[] objects = fieldExtractor.extract(dummyRow);
        Object[] expectedObjects = new Object[] { "100", "aaa-bb","phones_1","email_1","device_1","asof"};
        assertThat(objects, is(expectedObjects));
    }
}