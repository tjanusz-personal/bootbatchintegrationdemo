package com.batch.demo;

import com.batch.demo.configuration.DemoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SpringBootBatchIntegrationDemoApplication implements ApplicationRunner, ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(SpringBootBatchIntegrationDemoApplication.class);

	private ApplicationContext applicationContext;

	public static final String JOB_PARAM_CLIENTNAME = "clientName";
	public static final String JOB_PARAM_ASOFDATE = "asOfDate";
	public static final String JOB_PARAM_INPUT_FILE = "inputFileName";
	public static final String JOB_PARAM_OUTPUT_FILE = "outputFileName";

	@Autowired
	private DemoProperties properties;

	public static void main(String[] args) {
		// Old way to startup application (start springboot app using 'BatchConfiguration' properties)
        // Using this approach lets me override lots of dynamic stuff which would've been injected statically
		SpringApplication.run(BatchConfiguration.class,args);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		log.info("Application started with command-line arguments: " + Arrays.toString(args.getSourceArgs()));
		log.info("NonOptionArgs: " + args.getNonOptionArgs());
		log.info("OptionNames: " + args.getOptionNames());

		printOutProperties();

		// setup some dummy job arguments
		String clientName = "my_demo_client";
		String inFileName = "test_mappings.csv";
		String outFileName = "test_mappings_out.csv";
		Date asOfDate = new Date();

        // If have arguments passed then map them to the job values.
        // We can probably map these automatically to JobParameters but I wanted to see if/how we can intercept these calls.
        List<String> nonOptionArgs = args.getNonOptionArgs();
        if (nonOptionArgs.size() >= 1) {
            clientName = nonOptionArgs.get(0);
            inFileName = nonOptionArgs.get(1);
            outFileName = nonOptionArgs.get(2);
        }

        // make AsOfDate optional and be in milliseconds for now
        if (nonOptionArgs.size() >= 4) {
            String instantAsString = nonOptionArgs.get(3);
            Instant instant = Instant.ofEpochMilli(Long.valueOf(instantAsString));
            asOfDate = Date.from(instant);
        }

		// get application context so can configure job and run it
		ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) this.applicationContext;

		// this is starting the whole thing up (Note: I have job processing off by default in properties files for testing)
		SimpleJobLauncher jobLauncher = ctx.getBean(SimpleJobLauncher.class);

		// Setup some job parameters that would be passed into the job (asOfDate, inputFileResource, etc.)
		JobParameters jobParameters = new JobParametersBuilder()
				.addDate(JOB_PARAM_ASOFDATE, asOfDate)
				.addString(JOB_PARAM_CLIENTNAME, clientName)
				.addString(JOB_PARAM_INPUT_FILE, inFileName)
				.addString(JOB_PARAM_OUTPUT_FILE, outFileName)
				.toJobParameters();

		// Get main job bean (this is defined in our BatchConfiguration job as the Job to execute
		Job startDemoJob = ctx.getBean("startDemoJob", Job.class);

		// This starts the job
		JobExecution jobExecution = jobLauncher.run(startDemoJob, jobParameters);
    }

	private void printOutProperties() {
		log.info(this.properties.toDebugString());
		log.info("**** DEMO PROPERTIES END ****");
	}

}