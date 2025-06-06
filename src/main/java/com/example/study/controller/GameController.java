package com.example.study.controller;

import com.example.study.entity.Game;
import com.example.study.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @Autowired
    private GameRepository repository;

    @GetMapping("/game/{type}")
    public Game list(@PathVariable("type") final String type) {
        return repository.findByTitle(type)
                .orElseThrow(() -> new RuntimeException("game not found"));

    }
}
