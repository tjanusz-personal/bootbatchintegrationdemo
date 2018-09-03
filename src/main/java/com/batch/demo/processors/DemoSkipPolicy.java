package com.batch.demo.processors;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class DemoSkipPolicy implements SkipPolicy {

    private int maxSkipItems;

    public DemoSkipPolicy(int maxSkipItems) {
        this.maxSkipItems = maxSkipItems;
    }

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        StringBuilder builder = new StringBuilder();
        builder.append("DemoSkipPolicy - shouldSkip: Exception thrown!" );
        builder.append(" totalItemsSkipped: ");
        builder.append(skipCount);

        // just skip 5 errors to show what we can do here..
        if (skipCount < this.maxSkipItems) {
            builder.append(" BELOW MAX LIMIT, SKIPPING ITEM");
            System.out.println(builder.toString());
            return true;
        }

        builder.append(" OVER LIMIT, BLOWING CHUNKS ON WHOLE PROCESS!");
        System.out.println(builder.toString());
        return false;
    }
}
