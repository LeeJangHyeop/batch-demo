package com.mason.batch.demo.masonbatchdemo.repository;

import com.mason.batch.demo.masonbatchdemo.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
