package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.ColoredString;

import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;
import static it.polimi.ingsw.util.UTF.horizontal;

public abstract class CliCommon {
    private static final List<String> playerAnsiColors = Arrays.asList(
            Color.ANSI_YELLOW, Color.ANSI_CYAN, Color.ANSI_GREEN, Color.ANSI_WHITE, Color.ANSI_PURPLE
    );
    static final int nameLengthLimit = 20;

    private static final int canvasWidth = 175;
    private static final int canvasHeight = 48;

    static ColoredString[][] canvas;

    static String toAnsiColor(Player player) {
        return playerAnsiColors.get((player.getId() - 1) % 5);
    }

    static String nameOf(Player player) {
        String name = player.getName();
        if (name.length() <= nameLengthLimit)
            return name;
        return name.substring(0, nameLengthLimit - 4) + "...";
    }

    public static void write(int i, int j, ColoredString message) {
        for(int k = 0; k < message.content().length(); k ++) {
            canvas[i][j + k] = new ColoredString(message.content().substring(k, k + 1), message.color());
        }
    }

    public static void write(int i, int j, List<ColoredString> message) {
        int caret = 0;
        for(ColoredString cs : message) {
            for (int k = 0; k < cs.content().length(); k ++) {
                canvas[i][caret + j] = new ColoredString(cs.content().substring(k, k + 1), cs.color());
                caret ++;
            }
        }
    }

    static void frame(int i, int j, int width, int height, String ansiColor) {
        for(int cornerId = 1; cornerId <= 4; cornerId ++) {
            int ti = cornerId < 3 ? (i + height - 1) : i;
            int tj = cornerId == 2 || cornerId == 3 ? (j + width - 1) : j;

            String startingCorner = Arrays.asList(corner1, corner2, corner3, corner4).get(cornerId - 1);
            String line = cornerId % 2 == 0 ? vertical : horizontal;

            int limit = cornerId % 2 == 0 ? height : width;

            int di = Arrays.asList(0, -1, 0, 1).get(cornerId - 1);
            int dj = Arrays.asList(1, 0, -1, 0).get(cornerId - 1);

            canvas[ti][tj] = new ColoredString(startingCorner, ansiColor);
            for (int k = 1; k < limit - 1; k++) {
                ti += di;
                tj += dj;
                canvas[ti][tj] = new ColoredString(line, ansiColor);
            }
        }
    }

    public static ColoredString[][] getCanvas(Player player) {
        canvas = new ColoredString[canvasHeight][canvasWidth];
        CliTracks.build(player.getGame());
        CliBoard.build(player.getGame().getBoard());
        CliToasters.build(player);
        CliWeapons.build(player);
        CliPowerUps.build(player);
        return canvas;
    }
}
