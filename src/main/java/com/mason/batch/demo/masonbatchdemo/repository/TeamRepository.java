package com.mason.batch.demo.masonbatchdemo.repository;

import com.mason.batch.demo.masonbatchdemo.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
