package com.example.study.repository;

import com.example.study.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByTitle(final String title);
}
