package com.mason.batch.demo.masonbatchdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity(name = "player")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {
    @Id
    @GeneratedValue
    private Long id;

    private int number;

    private String name;

    private String description;

    private String status;

    @ManyToOne
    private Team team;

    public void setStatusToN() {
        this.status = "N";
    }
}
