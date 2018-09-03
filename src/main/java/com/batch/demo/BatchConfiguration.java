package com.batch.demo;

import com.batch.demo.configuration.DemoProperties;
import com.batch.demo.domain.DemoInputRow;
import com.batch.demo.processors.DemoItemProcessor;
import com.batch.demo.processors.DemoRetryListener;
import com.batch.demo.processors.DemoSkipListener;
import com.batch.demo.processors.DemoSkipPolicy;
import com.batch.demo.readers.DemoChunkListener;
import com.batch.demo.readers.DemoInputFieldSetMapper;
import com.batch.demo.readers.DemoItemReadListener;
import com.batch.demo.writers.DemoConsoleItemWriter;
import com.batch.demo.writers.DemoItemExtractor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Main configuration class for entire spring boot/batch processing. Lots of auto-wiring and spring batch framework
 * hookup is happening in here. I've tried to comment each part so I can remember later.
 */
@Configuration
@EnableBatchProcessing
@ComponentScan
@EnableConfigurationProperties({DemoProperties.class}) // need this so can automatically find the properties files
public class BatchConfiguration {

    // Spring needs a string location for the initial creation of the file reader and this dummy string is there
    // for code explanation purposes since this is a little bit of spring bean injection magic happening there.
    private static final String WILL_BE_INJECTED = "";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // Demo property value for various configuration defaults
    @Autowired
    private DemoProperties demoProperties;

    /**
     * Configuring batch to use simple FlatFileReader to read in the input.csv file
     */
    @Bean
    public FlatFileItemReader<DemoInputRow> buildDemoInputFileReader() {
        FlatFileItemReader<DemoInputRow> reader = new FlatFileItemReader<DemoInputRow>();
        reader.setLinesToSkip(1); // skip header row in file

        // Magic!
        reader.setResource(inputFileResource(WILL_BE_INJECTED));

        // Map every row in file to a DemoInputRow object using tokenizer definition below
        reader.setLineMapper(new DefaultLineMapper<DemoInputRow>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "recordId", "guid", "phones", "emails", "devices", "asOf" });
            }});
            // set our specific field mapper (how do we map each item from each CSV field to our object)
            setFieldSetMapper(new DemoInputFieldSetMapper());
        }});
        return reader;
    }

    /**
     * Configure our item processor for spring batch to call us for every row found by the reader.
     * Need StepScope because we're injecting some job parameters downstream (asOf, etc.).
     */
    @StepScope
    @Bean
    public DemoItemProcessor itemProcessor() {
        return new DemoItemProcessor();
    }

    /**
     * Configure how we're going to read our input. Currently using a FileSystemResource since it's much quicker than
     * reading the files over some remote location.
     * This is reading the jobParameters I have setup for the location of the file. This way it can easily be injected
     * into the job/code.
     */
    @Bean
    @StepScope
    public Resource inputFileResource(@Value("#{jobParameters[inputFileName]}") String inputFileName) {
        return new FileSystemResource(inputFileName);
    }

    /**
     * Define main Job to run. Single listener on completion, simple validator, single step to show what we can do here.
     */
    @Bean
    public Job startDemoJob() {
        // here is where we can configure the Job Start/Completion Listeners, or JobValidator
        return jobBuilderFactory.get("startDemoJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener())
                .validator(new DemoJobValidator())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        // Step configured to use 'chunk' processing, retry/skip logic, numerous listeners, task executors to demonstrate
        // what/how we can inject custom logic during each processing step.
        // Currently only writer is Console to debug locally.
        return stepBuilderFactory.get("step1")
                // figure out if want to use chunk or not chunk processing
                .<DemoInputRow, DemoInputRow> chunk(this.demoProperties.getDefaultChunkSize())
                .reader(buildDemoInputFileReader()).faultTolerant().skipPolicy(demoSkipPolicy())
                .retryPolicy(demoStepRetryPolicy())
                .listener(jobRetryListener())
                .listener(new DemoSkipListener())
                .listener(new DemoChunkListener())
                .listener(new DemoItemReadListener())
                .processor(itemProcessor())
                .writer(demoConsoleWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    /**
     * ConsoleWriter simply writes output to console and NOT to CSV file.
     */
    @Bean
    public ItemWriter demoConsoleWriter() {
        DemoConsoleItemWriter lineWriter = new DemoConsoleItemWriter();
        String exportFileHeader = "RecordId,Guid,Phones,Emails,Devices,Asof";
        lineWriter.setHeader(exportFileHeader);
        LineAggregator<DemoInputRow> lineAggregator = demoLineAggregator();
        lineWriter.setLineAggregator(lineAggregator);
        return lineWriter;
    }

    /**
     * LineAggregator basically tells the framework how to make a 'row' from one of our processing objects.
     */
    private LineAggregator<DemoInputRow> demoLineAggregator() {
        DelimitedLineAggregator<DemoInputRow> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(","); // comma separated values
        // This extractor knows how to pull out what fields we want in each row
        FieldExtractor<DemoInputRow> fieldExtractor = new DemoItemExtractor();
        lineAggregator.setFieldExtractor(fieldExtractor);
        return lineAggregator;
    }

    /**
     * Needed to configured jobLauncher so I can enable async processing using the taskExecutor.
     * I'm not sure this is the best way to do this but it's best I could find to start.
     */
    @Bean
    JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        jobLauncher.setTaskExecutor(taskExecutor());
        return jobLauncher;
    }

    /**
     * Configure out taskExecutor so we can enable async processing of each step. This has been implemented using
     * a multi-threaded step approach. (Run each step in different thread).
     */
    @Bean
    public TaskExecutor taskExecutor(){
        // This tries to run all the tasks as async as possible
        SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("spring_batch");
        asyncTaskExecutor.setConcurrencyLimit(this.demoProperties.getConcurrencyLimit());
        return asyncTaskExecutor;
    }

    /**
     * Simple do nothing transaction manager, naturally this would be different if using a real relational type database.
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    /**
     * Simple in-memory job repo for now.
     */
    @Bean
    public JobRepository jobRepository() throws Exception {
        return new MapJobRepositoryFactoryBean(transactionManager()).getJobRepository();
    }

    @Bean
    public JobExecutionListener jobCompletionListener() {
        return new JobCompletionNotificationListener();
    }

    /**
     * StepSkipper defines our rules for if/how we should skip a step during processing instead of crashing the whole job.
     */
    @Bean
    public SkipPolicy demoSkipPolicy() {
        return new DemoSkipPolicy(this.demoProperties.getMaxSkipItems());
    }

    /**
     * RetryPolicy defines our rules for if/how we retry running a step if any exceptions occur.
     */
    @Bean
    public RetryPolicy demoStepRetryPolicy() {
        // Hard code a basic retry policy for now..
        // Assume we'd be retrying network errors to services, etc.
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(this.demoProperties.getMaxRetryAttempts());
        return policy;
    }

    /**
     * Custom retry listener to demonstrate how can open/close/report errors
     */
    @Bean
    RetryListener jobRetryListener() {
        return new DemoRetryListener();
    }
}

