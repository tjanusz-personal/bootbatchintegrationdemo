package com.batch.demo.domain;

/**
 * Sample demo input row bean that is created from the batch input mapper.
 */
public class DemoInputRow {
    private String recordId;
    private String guid;
    private String phones;
    private String emails;
    private String devices;
    private String asOf;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getDevices() {
        return devices;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }

    public String getAsOf() {
        return asOf;
    }

    public void setAsOf(String asOf) {
        this.asOf = asOf;
    }

    public String toDebugString() {
        String debugString = String.join("\n\t",
                "*** DemoInputRow object ****",
                "recordId: " + this.getRecordId(),
        "guid: " + this.getGuid(),
        "phones: " + this.getPhones(),
        "emails: " + this.getEmails(),
        "devices: " + this.getDevices(),
        "asOf: " + this.getAsOf()
        );
        return debugString;
    }
}
