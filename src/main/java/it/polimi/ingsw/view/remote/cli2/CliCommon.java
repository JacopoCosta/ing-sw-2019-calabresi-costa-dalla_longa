package it.polimi.ingsw.view.remote.cli2;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.Color;

import java.util.Arrays;
import java.util.List;

public class CliCommon {
    private static final List<String> playerAnsiColors = Arrays.asList(Color.ANSI_YELLOW, Color.ANSI_CYAN, Color.ANSI_GREEN, Color.ANSI_WHITE, Color.ANSI_PURPLE);
    static final int nameLengthLimit = 20;

    static String toAnsiColor(Player player) {
        return playerAnsiColors.get((player.getId() - 1) % 5);
    }

    static String nameOf(Player player) {
        String name = player.getName();
        if (name.length() <= nameLengthLimit)
            return name;
        return name.substring(0, nameLengthLimit - 4) + "...";
    }
}
