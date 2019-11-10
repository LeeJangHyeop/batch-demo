package com.mason.batch.demo.masonbatchdemo.multithreadedStepsjob;

import com.mason.batch.demo.masonbatchdemo.domain.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;

@Configuration
@Slf4j
public class AsyncProcessorAndWriterJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean("asyncProcessorAndWriterJob")
    public Job job() {
        return jobBuilderFactory
                .get("asyncProcessorAndWriterJob")
                .incrementer(new RunIdIncrementer())
                .start(asyncProcessorAndWriterJobStep())
                .build();
    }

    @Bean("asyncProcessorAndWriterJobStep")
    public Step asyncProcessorAndWriterJobStep() {
        return stepBuilderFactory.get("asyncProcessorAndWriterJobStep")
                .allowStartIfComplete(true)
                .<Player, Player>chunk(50)
                .reader(asyncProcessorAndWriterJobReader())
                .processor((ItemProcessor) asyncProcessorAndWriterJobAsyncProcessor())
                .writer(asyncProcessorAndWriterJobAsyncWriter())
                .build();
    }

    @Bean("asyncProcessorAndWriterJobReader")
    @StepScope
    public JpaPagingItemReader<Player> asyncProcessorAndWriterJobReader() {
        JpaPagingItemReader<Player> reader = new JpaPagingItemReader<>();

        String query = "SELECT p FROM player p ORDER BY p.id ASC";

        reader.setQueryString(query);
        reader.setPageSize(50);
        reader.setEntityManagerFactory(entityManagerFactory);
        return reader;
    }

    @Bean("asyncProcessorAndWriterJobProcessor")
    @StepScope
    public ItemProcessor<Player, Player> asyncProcessorAndWriterJobProcessor() {
        return player -> {
            log.info("player id : " + player.getId());
            player.setStatusToN();
            return player;
        };
    }

    @Bean("asyncProcessorAndWriterJobAsyncProcessor")
    @StepScope
    public AsyncItemProcessor<Player, Player> asyncProcessorAndWriterJobAsyncProcessor() {
        AsyncItemProcessor<Player, Player> processor = new AsyncItemProcessor<>();
        processor.setDelegate(asyncProcessorAndWriterJobProcessor());
        processor.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return processor;
    }

    @Bean("asyncProcessorAndWriterJobWriter")
    @StepScope
    public JpaItemWriter<Player> asyncProcessorAndWriterJobWriter() {
        JpaItemWriter<Player> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean("asyncProcessorAndWriterJobAsyncWriter")
    @StepScope
    public AsyncItemWriter<Player> asyncProcessorAndWriterJobAsyncWriter() {
        AsyncItemWriter<Player> writer = new AsyncItemWriter<>();
        writer.setDelegate(asyncProcessorAndWriterJobWriter());
        return writer;
    }
}
