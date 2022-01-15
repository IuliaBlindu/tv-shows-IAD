package com.iad.tvshowsiad.result;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class ListOfGames implements Serializable {
    @Getter
    @Setter
    private static String category;
    @Getter
    private static final ArrayList<Game> games = new ArrayList<>();

    public static void addGame (Game game) {
        games.add(game);
    }


}
