package com.batch.demo.processors;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DemoSkipPolicyTest {

    private DemoSkipPolicy policy;

    @Before
    public void setup() {
        policy = new DemoSkipPolicy(3);
    }

    @Test
    public void shouldSkipFirstThreeExceptions() {
        Integer[] intArray = { 1,2 };
        List<Integer> lb = Arrays.asList(intArray);
        lb.forEach(skipCount -> {
            assertThat(policy.shouldSkip(new Throwable(), skipCount), is(true));
        });
    }

    @Test
    public void shouldSkipFailsOnFourthException() {
        assertThat(policy.shouldSkip(new Throwable(), 3), is(false));
    }

    @Test
    public void shouldSkipFailsOnSkipCountGreaterThan4() {
        Integer[] intArray = { 6,7,10 };
        List<Integer> lb = Arrays.asList(intArray);
        lb.forEach(skipCount -> {
            assertThat(policy.shouldSkip(new Throwable(), skipCount), is(false));
        });
    }

}