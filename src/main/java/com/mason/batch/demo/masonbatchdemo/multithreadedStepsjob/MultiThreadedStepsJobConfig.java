package com.mason.batch.demo.masonbatchdemo.multithreadedStepsjob;

import com.mason.batch.demo.masonbatchdemo.domain.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;

@Configuration
@Slf4j
public class MultiThreadedStepsJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean("multiThreadedStepsJob")
    public Job job() {
        return jobBuilderFactory
                .get("multiThreadedStepsJob")
                .incrementer(new RunIdIncrementer())
                .start(multiThreadedStepsJobStep())
                .build();
    }

    @Bean("multiThreadedStepsJobStep")
    public Step multiThreadedStepsJobStep() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.afterPropertiesSet();

        return stepBuilderFactory.get("multiThreadedStepsJobStep")
                .allowStartIfComplete(true)
                .<Player, Player>chunk(50)
                .reader(multiThreadedStepsJobReader())
                .processor(multiThreadedStepsJobProcessor())
                .writer(multiThreadedStepsJobWriter())
                .taskExecutor(executor)
                .build();
    }

    @Bean("multiThreadedStepsJobReader")
    @StepScope
    public JpaPagingItemReader<Player> multiThreadedStepsJobReader() {
        JpaPagingItemReader<Player> reader = new JpaPagingItemReader<>();

        String query = "SELECT p FROM player p ORDER BY p.id ASC";

        reader.setQueryString(query);
        reader.setPageSize(50);
        reader.setEntityManagerFactory(entityManagerFactory);
        return reader;
    }

    @Bean("multiThreadedStepsJobProcessor")
    @StepScope
    public ItemProcessor<Player, Player> multiThreadedStepsJobProcessor() {
        return player -> {
            log.info("player id : " + player.getId());
            player.setStatusToN();
            return player;
        };
    }

    @Bean("multiThreadedStepsJobWriter")
    @StepScope
    public JpaItemWriter<Player> multiThreadedStepsJobWriter() {
        JpaItemWriter<Player> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
