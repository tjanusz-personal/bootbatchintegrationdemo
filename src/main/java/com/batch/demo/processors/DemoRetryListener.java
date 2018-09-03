package com.batch.demo.processors;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public class DemoRetryListener implements RetryListener {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        // do any open work in here..
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        // do any close work in here..
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        // demonstrate how can listen for when a retry happens..
        StringBuilder builder = new StringBuilder();
        builder.append("DemoRetryListener - onError: Something thrown! retryCount: ");
        builder.append(context.getRetryCount());
        builder.append(" ErrorInfo: ");
        builder.append(throwable.toString());
        System.out.println(builder.toString());
    }
}
