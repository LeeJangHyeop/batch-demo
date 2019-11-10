package com.mason.batch.demo.masonbatchdemo.multithreadedStepsjob;

import com.mason.batch.demo.masonbatchdemo.domain.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
public class ParallelStepsJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean("parallelStepsJob")
    public Job parallelStepsJob() {
        Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                .start(secondStep())
                .build();

        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(firstStep())
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .build();

        return jobBuilderFactory.get("parallelStepsJob")
                .start(parallelFlow)
                .end()
                .build();
    }

    @Bean("firstStep")
    public Step firstStep() {
        return stepBuilderFactory.get("firstStep")
                .allowStartIfComplete(true)
                .<Player, Player>chunk(50)
                .reader(firstReader())
                .processor(parallelStepsJobProcessor())
                .writer(parallelStepsJobWriter())
                .build();
    }

    @Bean("firstReader")
    @StepScope
    public JpaPagingItemReader<Player> firstReader() {
        JpaPagingItemReader<Player> reader = new JpaPagingItemReader<>();
        String query = "SELECT p FROM player p WHERE p.status = 'Y' AND p.id >= 0 AND p.id < 50000 ORDER BY p.id ASC";

        reader.setQueryString(query);
        reader.setPageSize(50);
        reader.setEntityManagerFactory(entityManagerFactory);
        return reader;
    }

    @Bean("secondStep")
    public Step secondStep() {
        return stepBuilderFactory.get("secondStep")
                .allowStartIfComplete(true)
                .<Player, Player>chunk(50)
                .reader(secondReader())
                .processor(parallelStepsJobProcessor())
                .writer(parallelStepsJobWriter())
                .build();
    }

    @Bean("secondReader")
    @StepScope
    public JpaPagingItemReader<Player> secondReader() {
        JpaPagingItemReader<Player> reader = new JpaPagingItemReader<>();
        String query = "SELECT p FROM player p WHERE p.status = 'Y' AND p.id >= 50000 AND p.id < 100000 ORDER BY p.id ASC";

        reader.setQueryString(query);
        reader.setPageSize(50);
        reader.setEntityManagerFactory(entityManagerFactory);
        return reader;
    }

    @Bean("parallelStepsJobProcessor")
    @StepScope
    public ItemProcessor<Player, Player> parallelStepsJobProcessor() {
        return player -> {
            log.info("player id : " + player.getId());
            player.setStatusToN();
            return player;
        };
    }

    @Bean("parallelStepsJobWriter")
    @StepScope
    public JpaItemWriter<Player> parallelStepsJobWriter() {
        JpaItemWriter<Player> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
