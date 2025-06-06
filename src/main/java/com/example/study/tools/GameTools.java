package com.example.study.tools;


import com.example.study.entity.Game;
import com.example.study.entity.GameComplexity;
import com.example.study.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameTools {

    private final GameRepository gameRepository;

    public GameTools(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GameTools.class);

    @Tool(name = "getGameComplexity",
            description = "Returns a game's complexity/difficulty " +
                    "given the game's title/name.")
    public GameComplexityResponse getGameComplexity(
            @ToolParam(description="The title of the game") //
            String gameTitle) {
        String gameSlug = gameTitle    //
                .toLowerCase()
                .replace(" ", "_");

        LOGGER.info("Getting complexity for {} ({})",
                gameTitle, gameSlug);

        Optional<Game> gameOpt = gameRepository.findByTitle(gameSlug);

        Game game = gameOpt.orElseGet(() -> {
            LOGGER.warn("Game not found: {}", gameSlug);
            return new Game(
                    null,
                    gameSlug,
                    gameTitle,
                    GameComplexity.UNKNOWN.getValue());
        });

        return new GameComplexityResponse(
                game.getTitle(), game.complexityEnum());
    }

}