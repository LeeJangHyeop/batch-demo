package com.mason.batch.demo.masonbatchdemo;

import com.mason.batch.demo.masonbatchdemo.domain.Player;
import com.mason.batch.demo.masonbatchdemo.repository.PlayerRepository;
import com.mason.batch.demo.masonbatchdemo.repository.TeamRepository;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableBatchProcessing
@EnableWebFlux
public class MasonBatchDemoApplication {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    public static void main(String[] args) {
        SpringApplication.run(MasonBatchDemoApplication.class, args);
    }

    @PostConstruct
    public void init() {
//        IntStream.range(0, 100000)
//                .forEach(i -> {
//                    Player p = Player.builder()
//                            .name("test" + i)
//                            .number(i)
//                            .description("my name is test" + i)
//                            .status("Y")
//                            .build();
//                    playerRepository.save(p);
//                });
    }
}
