package com.mason.batch.demo.masonbatchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/job")
public class ExecutorController {

    @Autowired
    @Qualifier("multiThreadedStepsJob")
    private Job multiThreadedStepsJob;

    @Autowired
    @Qualifier("parallelStepsJob")
    private Job parallelStepsJob;

    @Autowired
    @Qualifier("asyncProcessorAndWriterJob")
    private Job asyncProcessorAndWriterJob;


    @Autowired
    private JobLauncher jobLauncher;

    @GetMapping("/multiThreadedStepsJob")
    public Mono<String> multiThreadedStepsJobExecute() throws Exception{
        JobExecution jobExecution = jobLauncher.run(multiThreadedStepsJob, new JobParameters());
        return Mono.just(jobExecution.getStatus().toString());
    }

    @GetMapping("/parallelStepsJob")
    public Mono<String> parallelStepsJobExecute() throws Exception{
        JobExecution jobExecution = jobLauncher.run(parallelStepsJob, new JobParameters());
        return Mono.just(jobExecution.getStatus().toString());
    }

    @GetMapping("/asyncProcessorAndWriterJob")
    public Mono<String> asyncProcessorAndWriterJob() throws Exception{
        JobExecution jobExecution = jobLauncher.run(asyncProcessorAndWriterJob, new JobParameters());
        return Mono.just(jobExecution.getStatus().toString());
    }
}
