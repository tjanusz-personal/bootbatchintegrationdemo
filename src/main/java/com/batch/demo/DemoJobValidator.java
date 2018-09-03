package com.batch.demo;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.util.Date;

import static com.batch.demo.SpringBootBatchIntegrationDemoApplication.JOB_PARAM_ASOFDATE;
import static com.batch.demo.SpringBootBatchIntegrationDemoApplication.JOB_PARAM_INPUT_FILE;
import static com.batch.demo.SpringBootBatchIntegrationDemoApplication.JOB_PARAM_OUTPUT_FILE;

public class DemoJobValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        validateAsOfDate(parameters);
        validateInputFileName(parameters);
        validateOutputFileName(parameters);
    }

    void validateOutputFileName(JobParameters parameters) throws JobParametersInvalidException {
        String outputFileName = parameters.getString(JOB_PARAM_OUTPUT_FILE);
        if (StringUtils.isEmpty(outputFileName)) {
            throw new JobParametersInvalidException("invalid outputFileName. Must be non empty string");
        }
    }

    void validateInputFileName(JobParameters parameters) throws JobParametersInvalidException {
        String inputFileName = parameters.getString(JOB_PARAM_INPUT_FILE);
        if (StringUtils.isEmpty(inputFileName)) {
            throw new JobParametersInvalidException("invalid inputFileName. Must be non empty string");
        }
    }

    void validateAsOfDate(JobParameters parameters) throws JobParametersInvalidException {
        Date asOfDate = parameters.getDate(JOB_PARAM_ASOFDATE);
        if (asOfDate == null || asOfDate.after(new Date())) {
            throw new JobParametersInvalidException("invalid asOfDate must be non-null or before current time");
        }
    }

}
