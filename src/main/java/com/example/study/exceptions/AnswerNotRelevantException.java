package com.example.study.exceptions;

public class AnswerNotRelevantException extends RuntimeException {


    public AnswerNotRelevantException(final String msg, final String text) {
        super("The answer '" + msg + "' is not relevant to the question '" + text + "'.");
    }

}
