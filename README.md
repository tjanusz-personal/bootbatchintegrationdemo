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

## Docker container execution

Since most of the final design is TBD this demo only does basics (e.g. build, run, pass arguments). 
This assumes docker is installed locally. For example you can run `docker -v` and see docker output.

### Build the docker container image

`gradle buildDocker` (builds image using dockerfile in project)

Note there is no specific version being built so a new image is built every time buildDocker is run.

* `docker images` (see all images)
* `docker rmi ca8192c87ea1` (remove specific image)
* `docker system prune` (removes all unused stuff - images, containers, volumes). This is super helpful to reclaim disk
space. But be sure to understand it nukes pretty much everything not used!

### Docker and Local File System 
Once image is built there are two ways to configure access to the CSV files on the docker local file system
1. Copy input.csv file into container and run the container
OR
2. Start container with mounted volume to host machine

### Copy input.csv file into container
Steps include:
* Create container with job parameter values
* Copy input.csv file into container (using docker cp command)
* Run the container

**Create the container with job parameter values**
Use the docker create command to create an container named 'batchdemo'
`docker create --name "batchdemo" -e JAVA_ARGS="demotest test_mappings.csv test_mappings_out.csv" com.batch/bootbatchintegrationdemo`

Note: the JAVA_ARGS are same as the command line arguments and the first 3 must be specified.

**Copy the input.csv file into the container**
Uses the docker COPY command to copy the `test_mappings.csv` to the containers file system
`docker cp test_mappings.csv batchdemo:test_mappings.csv`

**Run the container**
Start the named docker container in interactive mode which automatically runs the batch job
`docker start batchdemo -i`

### Start container with mounted volume to host machine
Another way to pass the input.csv file to the running job is to use the docker mount command during 
execution and point this to a known location on the host file system.
 
This approach may be used if we have a common volume for all jobs.
`docker run -v /Users/timjanusz/Documents/temp:/tmp -e JAVA_ARGS="demotest /tmp/test_mappings.csv test_mappings_out.csv" -i com.batch/bootbatchintegrationdemo`

In the command above:
* The 'test_mappings.csv' input file is located on the local hard drive in the `/Users/timjanusz/Documents/temp` folder 
* Mounted volume is located in the container's `/tmp` directory 
* Command line arguments MUST use CONTAINER path as the location to find the csv file `/tmp/test_mappings.csv`

### Copy the output.csv from the container to local file system
Once the job completes the output.csv file is still located in the container's file system and must be extracted to the local file system.
`docker cp batchdemo:test_mappings_out.csv test_mappings_out.csv` 


## TODO ITEMS
* **S3 integration** for file locations (may make things easier when integrated w/in AWS Batch) need to figure out better 
approach to containerize the S3 integration
* **AWS Batch execution** (ability to run this as a AWS Batch job)
* **REST service integration** (make sample REST calls to service to mimic eventual calls to REST services)
* **JobRepository** Investigate integration into real JobRepository database what do we get here?