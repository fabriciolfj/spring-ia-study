package com.example.study.controller;


import com.example.study.model.Answer;
import com.example.study.model.Question;
import com.example.study.service.BoardGameService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AskController {

    private final BoardGameService boardGameService;

    public AskController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping("/ask")
    public Answer ask(@RequestBody @Valid Question question,
                      @RequestHeader(name = "X_AI_CONVERSATION_ID", defaultValue = "padrao")
                      final String conversationId) {
        return boardGameService.askQuestion(question, conversationId);
    }

}