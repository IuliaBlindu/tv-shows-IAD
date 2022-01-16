package com.iad.tvshowsiad;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iad.tvshowsiad.result.Game;
import lombok.experimental.UtilityClass;
import org.apache.camel.Exchange;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class Utilities {

    public static final String CATEGORY = "{{category}}";
    public static final String JSON_EXTENSION = ".json";
    public static final String SUBJECT = CATEGORY + " Games";
    public static final String GAMES_NUMBER = "{{games_number}}";
    public static final String LIST_OF_GAMES = "{{list_of_games}}";
    public static final String MESSAGE = "Hello,\n\nWe found " + GAMES_NUMBER + " games.\nHere is the list:\n" + LIST_OF_GAMES;

    public Exchange getGame(Exchange oldExchange, Exchange newExchange) {
        Map<String, Object> body = newExchange.getIn().getBody(Map.class);

        String title = (String) body.get("title");
        String platform = (String) body.get("platform");

        List<Game> games ;
        Exchange exchange;

        if (oldExchange == null) {
            games = new ArrayList<>();
            newExchange.getIn().setBody(games);
            exchange = newExchange;
        } else {
            games = oldExchange.getIn().getBody(List.class);
            exchange = oldExchange;
        }
        games.add(Game.builder().title(title).platform(platform).build());
        return exchange;
    }

    public static String getCategory(String fileName) {
        return fileName.split(JSON_EXTENSION)[0];
    }

    public static String replaceSubject(String category) {
        return SUBJECT.replace(CATEGORY, category);
    }

    public static String replaceMessage(String body) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Game> games = objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, Game.class));
        String message = MESSAGE.replace(GAMES_NUMBER, String.valueOf(games.size()));

        StringBuilder gamesBuilder = new StringBuilder();

        for (int i = 0; i < games.size(); i++) {
            gamesBuilder.append(i + 1).append(". ");
            gamesBuilder.append(games.get(i).toString());
            gamesBuilder.append("\n");
        }
        return message.replace(LIST_OF_GAMES, gamesBuilder.toString());
    }
}
