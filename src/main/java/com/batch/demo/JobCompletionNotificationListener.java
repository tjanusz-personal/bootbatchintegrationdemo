package com.batch.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    public JobCompletionNotificationListener() {
    }

    /**
     * Simple before Job injection point. Can add anything we want in here to happen before a job starts.
     * @param jobExecution
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("*** BEFORE JOB Status is: " + jobExecution.getStatus());
    }

    /**
     * Simple after job injection point. Can add anything we want to happen after a job is completed.
     * For example, in one application we were copying the files to a different location on success.
     * @param jobExecution
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("*** AFTER JOB Status is: " + jobExecution.getStatus());

        // can check for status is failed, complete etc. and do stuff in here..
        jobExecution.getStepExecutions().forEach( stepExecution -> {
            log.info("*** AFTER JOB Step summary: " + stepExecution.getSummary());
        });

        // Can get access to current running job parameters to do stuff with them.
        String clientName = jobExecution.getJobParameters().getString(SpringBootBatchIntegrationDemoApplication.JOB_PARAM_CLIENTNAME);
        String outputFileName = jobExecution.getJobParameters().getString(SpringBootBatchIntegrationDemoApplication.JOB_PARAM_OUTPUT_FILE);
    }


}