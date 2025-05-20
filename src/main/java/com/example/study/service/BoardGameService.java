package com.example.study.service;

import com.example.study.model.Answer;
import com.example.study.model.Question;

public interface BoardGameService {

    Answer askQuestion(final Question question);
}
