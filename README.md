# Boot Batch Integration Demo

This project is a sample Spring Batch application (using spring boot) to demonstrate how to hook up all the various
things a spring batch application can configure but in a spring boot way.

Features:
* **InputReading** of simple CSV file (local file system only)
* **OutputWriting** of simple CSV output to console
* **JobParameters** - Single batch job configured to run with steps accepting various JobParameters
* **CHUNK processing** - for large files
* **ASYNC processing** using MULTI_THREADED_STEP approach
* **Step Retry/Skipping** - examples with dummy exception handling. Current implementation purposely throws some exceptions
to demonstrate how retry/skip logic works with the framework.
* **Sample Unit/Integration tests** for each 'layer'
* **IN_MEMORY JobRepository** (not external database connection)


## Dependencies
* Junit - to run the unit tests
* Java 8 - I use streams in a lot of the processing to simplify my life
* Gradle - configuration/build


## Installation
This project requires using Java 8.

This project requires Gradle for all build/run activities. A wrapper that you can use is included
with the project, but you can also install gradle locally via brew install gradle.


## Configuration Properties
There are sample core configuration properties defined for usage in the jobs. Some properties are just placeholders 
for examples (e.g. endpoint urls) whereas others are actually used in the codebase (defaultChunkSize). 
These are all mapped to a single POJO class (DemoProperties) using the @ConfigurationProperties("bootbatchintegrationdemo")
annotations. 

|Property|Example|
|---|---|
|`bootbatchintegrationdemo.restURLLocation`|http://google.com|
|`bootbatchintegrationdemo.defaultChunkSize`|10|
|`bootbatchintegrationdemo.concurrencyLimit`|5|
|`bootbatchintegrationdemo.maxRetryAttempts`|6|
|`bootbatchintegrationdemo.maxSkipItems`|3|


## Execution
Simple hard coded main method with no arguments (RUN -> SpringbatchdemoApplication)
 
Note there are four command line arguments accepted. The first three are required with the last one being optional (AsOfDate). 
Order is mandatory!

|Argument|Desc|Default|
|---|---|---|
|Client Name|name of client to run (placeholder)|demoTest|
|Input File Name|fully qualified path to input.csv file to read|test_mappings.csv|
|Output File Name|name of output file to write output|test_mappings_3_out.csv|
|AsOfDate|time in milliseconds for asOfDate|1502885612000|


**If no arguments are passed, the code is hard coded looking for an input file named test_mappings.csv in the root of the project!**

## Misc Notes
* The CSV processing is limited to simple CSV files (no embedded commas in fields) but that's OK since my sample data set is restricted to that type


## TODO ITEMS
* **Docker integration** for integrating the service as a docker container. I have previously figured this out and will add this to this
project at some point.
* **S3 integration** for file locations (may make things easier when integrated w/in AWS Batch) need to figure out better 
approach to containerize the S3 integration
* **AWS Batch execution** (ability to run this as a AWS Batch job)
* **REST service integration** (make sample REST calls to service to mimic eventual calls to REST services)
* **JobRepository** Investigate integration into real JobRepository database what do we get here?