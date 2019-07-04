package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.ColoredString;

import java.util.Arrays;
import java.util.List;

public abstract class CliCommon {
    private static final List<String> playerAnsiColors = Arrays.asList(Color.ANSI_YELLOW, Color.ANSI_CYAN, Color.ANSI_GREEN, Color.ANSI_WHITE, Color.ANSI_PURPLE);
    static final int nameLengthLimit = 20;

    private static final int canvasWidth = 192;
    private static final int canvasHeight = 48;

    static ColoredString[][] canvas = new ColoredString[canvasHeight][canvasWidth];

    static String toAnsiColor(Player player) {
        return playerAnsiColors.get((player.getId() - 1) % 5);
    }

    static String nameOf(Player player) {
        String name = player.getName();
        if (name.length() <= nameLengthLimit)
            return name;
        return name.substring(0, nameLengthLimit - 4) + "...";
    }

    public static ColoredString[][] getCanvas() {
        return canvas;
    }
}
