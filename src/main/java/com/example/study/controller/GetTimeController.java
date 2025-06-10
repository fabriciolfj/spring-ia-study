package com.example.study.controller;

import com.example.study.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetTimeController {

    private static final String CURRENT_TIME_TEMPLATE = "qual a hora atual da {city}?";

    private final ChatClient chatClient;
    private final TimeTools timeTools;

    public GetTimeController(final ChatClient.Builder chatClientBuilder, final TimeTools timeTools) {
        this.timeTools = timeTools;
        this.chatClient =  chatClientBuilder
                .defaultTools(timeTools)
                .build();
    }

    @GetMapping("/time")
    public String getTime(@RequestParam("city") final String city) {
        return chatClient.prompt()
                .user(userSpec -> {
                    userSpec.text(CURRENT_TIME_TEMPLATE)
                            .param("city", city);
                })
                .call()
                .content();

    }
}
