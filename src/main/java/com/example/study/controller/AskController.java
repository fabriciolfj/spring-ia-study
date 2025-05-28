package com.example.study.controller;


import com.example.study.model.Answer;
import com.example.study.model.Question;
import com.example.study.service.BoardGameService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping
public class AskController {

    private final BoardGameService boardGameService;

    public AskController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping("/ask")
    public Flux<String> ask(@RequestBody @Valid Question question) {
        return boardGameService.askQuestion(question);
    }

}