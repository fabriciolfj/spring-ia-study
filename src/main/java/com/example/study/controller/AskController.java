package com.example.study.controller;


import com.example.study.model.Answer;
import com.example.study.model.Question;
import com.example.study.service.BoardGameService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AskController {

    private final BoardGameService boardGameService;

    public AskController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping("/ask")
    public Answer ask(@RequestBody Question question) {
        return boardGameService.askQuestion(question);
    }

}