package com.batch.demo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Main properties bean to capture all the generic properties common to all runs.
 * This is how you can easily configure these for all runs.
 */
@ConfigurationProperties("bootbatchintegrationdemo")
public class DemoProperties {

    private int defaultChunkSize;
    private int maxSkipItems;
    private int maxRetryAttempts;
    private String restURLLocation;
    private int concurrencyLimit;

    public int getDefaultChunkSize() {
        return defaultChunkSize;
    }

    public void setDefaultChunkSize(int defaultChunkSize) {
        this.defaultChunkSize = defaultChunkSize;
    }

    public String getRestURLLocation() {
        return restURLLocation;
    }

    public void setRestURLLocation(String restURLLocation) {
        this.restURLLocation = restURLLocation;
    }

    public int getMaxSkipItems() {
        return maxSkipItems;
    }

    public void setMaxSkipItems(int maxSkipItems) {
        this.maxSkipItems = maxSkipItems;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public int getConcurrencyLimit() {
        return concurrencyLimit;
    }

    public void setConcurrencyLimit(int concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }


    public String toDebugString() {
        String debugString = String.join("\n\t",
                "**** CUSTOM PROPERTIES START ****",
                "bootbatchintegrationdemo.restURLLocation: " + this.getRestURLLocation(),
                "bootbatchintegrationdemo.maxRetryAttempts: " + this.getMaxRetryAttempts(),
                "bootbatchintegrationdemo.maxSkipItems: " + this.getMaxSkipItems(),
                "bootbatchintegrationdemo.concurrencyLimit: " + this.getConcurrencyLimit(),
                "bootbatchintegrationdemo.defaultChunkSize: " + this.getDefaultChunkSize()
        );
        return debugString;
    }

}
