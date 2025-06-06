package com.example.study.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    private Long id;
    private String slug;
    private String title;
    private float complexity;

    public GameComplexity complexityEnum() {
        int rounded = Math.round(complexity);
        return GameComplexity.values()[rounded];
    }
}