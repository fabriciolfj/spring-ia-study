package com.example.study.service;

import com.example.study.model.Answer;
import com.example.study.model.Question;
import reactor.core.publisher.Flux;

public interface BoardGameService {

    Flux<String> askQuestion(final Question question);
}
