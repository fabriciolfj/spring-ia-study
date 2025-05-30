package com.example.study.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GameRulesService {

    private static final Logger LOG =
            LoggerFactory.getLogger(GameRulesService.class);

    private final VectorStore vectorStore;

    public GameRulesService(final VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String getRulesFor(String gameName, String question) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(20) //quantidade de documentos com base na similaridade
                .similarityThreshold(0.1) //nivel de similaridade (maior refinamento)
                //.filterExpression(new FilterExpressionBuilder()
                 //       .eq("gameTitle", normalizeGameTitle(gameName)).build())
                .build();

        log.info("search request: {}", searchRequest);

        var similarDocs = vectorStore.similaritySearch(searchRequest);

        if (similarDocs.isEmpty()) {
            return "as regras para " + gameName + " nao estao disponiveis";
        }

        return similarDocs.stream().map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator())); //unindo por quebra de linha

    }

    private String normalizeGameTitle(final String gameTitle) {
        return gameTitle.toLowerCase().replace(" ", "_");
    }

    /*
    public String getRulesFor(String gameName) {
        try {
            String filename = String.format(
                    "classpath:/gameRules/%s.txt",
                    gameName.toLowerCase().replace(" ", "_")); //

            return new DefaultResourceLoader()
                    .getResource(filename)
                    .getContentAsString(Charset.defaultCharset()); //
        } catch (IOException e) {
            LOG.info("No rules found for game: " + gameName);
            return "";                                            //
        }
    }*/

}