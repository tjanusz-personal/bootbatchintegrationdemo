package com.batch.demo;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.batch.demo.SpringBootBatchIntegrationDemoApplication.JOB_PARAM_ASOFDATE;
import static com.batch.demo.SpringBootBatchIntegrationDemoApplication.JOB_PARAM_INPUT_FILE;
import static com.batch.demo.SpringBootBatchIntegrationDemoApplication.JOB_PARAM_OUTPUT_FILE;

public class DemoJobValidatorTest {

    private DemoJobValidator demoJobValidator;

    @Before
    public void setUp() {
        demoJobValidator = new DemoJobValidator();
    }

    @Test
    public void contextLoads() {
    }

    @Test(expected = JobParametersInvalidException.class)
    public void validateOutputFileNameThrowsJobParametersInvalidExceptionWithEmptyString() throws Exception {
        demoJobValidator.validateOutputFileName(dummyJobParameters(JOB_PARAM_OUTPUT_FILE, ""));
    }

    @Test
    public void validateOutputFileNameValidatesAnyNonEmptyString() throws Exception {
        demoJobValidator.validateOutputFileName(dummyJobParameters(JOB_PARAM_OUTPUT_FILE, "testFileName"));
    }

    @Test(expected = JobParametersInvalidException.class)
    public void validateInputFileNameThrowsJobParametersInvalidExceptionWithEmptyString() throws Exception {
        demoJobValidator.validateInputFileName(dummyJobParameters(JOB_PARAM_INPUT_FILE, ""));
    }

    @Test
    public void validateInputFileNameValidatesAnyNonEmptyString() throws Exception {
        demoJobValidator.validateInputFileName(dummyJobParameters(JOB_PARAM_INPUT_FILE, "testFileName"));
    }

    @Test(expected = JobParametersInvalidException.class)
    public void validateAsOfDateNameThrowsJobParametersInvalidExceptionWithNullDate() throws Exception {
        Date asOfDate = null;
        demoJobValidator.validateAsOfDate(dummyJobParameters(JOB_PARAM_ASOFDATE, asOfDate));
    }

    @Test(expected = JobParametersInvalidException.class)
    public void validateAsOfDateNameThrowsJobParametersInvalidExceptionWithDateInFuture() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        Date asOfDate = Date.from(currentTime.plusDays(2).atZone(ZoneId.systemDefault()).toInstant());
        demoJobValidator.validateAsOfDate(dummyJobParameters(JOB_PARAM_ASOFDATE, asOfDate));
    }

    @Test
    public void validateAsOfDateValidatesAnyNonDate() throws Exception {
        demoJobValidator.validateAsOfDate(dummyJobParameters(JOB_PARAM_ASOFDATE, new Date()));
    }

    @Test
    public void validateExpectsAsOfDateAndInputFileName() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(JOB_PARAM_INPUT_FILE, "InputFileName")
                .addString(JOB_PARAM_OUTPUT_FILE, "OutputFileName")
                .addDate(JOB_PARAM_ASOFDATE, new Date())
                .toJobParameters();
        demoJobValidator.validate(jobParameters);
    }

    private JobParameters dummyJobParameters(String key, String value) {
        JobParameters jobParameters = new JobParametersBuilder().addString(key, value).toJobParameters();
        return jobParameters;
    }

    private JobParameters dummyJobParameters(String key, Date value) {
        JobParameters jobParameters = new JobParametersBuilder().addDate(key, value).toJobParameters();
        return jobParameters;
    }
}