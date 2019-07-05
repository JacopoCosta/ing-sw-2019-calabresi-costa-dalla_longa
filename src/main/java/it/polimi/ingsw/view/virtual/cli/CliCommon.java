package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.printer.Color;
import it.polimi.ingsw.util.printer.ColoredString;
import it.polimi.ingsw.view.remote.cli.CLI;

import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;
import static it.polimi.ingsw.util.UTF.horizontal;

/**
 * This class collects various properties and utility methods for building the coloured {@link CLI} interface.
 */
public abstract class CliCommon {
    /**
     * A list containing all the {@code ANSI} escape sequences corresponding to the five {@link Player} token colours.
     */
    private static final List<String> playerAnsiColors = Arrays.asList(
            Color.YELLOW, Color.CYAN, Color.GREEN, Color.WHITE, Color.PURPLE
    );

    /**
     * The length of a {@link Player}'s name beyond which it gets truncated and appended with ellipses
     * such that it can fit inside the interface boxes.
     */
    static final int nameLengthLimit = 20;

    /**
     * The width of the interface, expressed in characters.
     */
    private static final int canvasWidth = 175;

    /**
     * The height of the interface, expressed in characters.
     */
    private static final int canvasHeight = 48;

    /**
     * The grid used to draw the interface's objects.
     */
    static ColoredString[][] canvas;

    /**
     * Tells which colour a {@link Player}'s token is.
     * @param player the player.
     * @return the {@code ANSI} escape sequence for that colour.
     */
    static String toAnsiColor(Player player) {
        return playerAnsiColors.get((player.getId() - 1) % 5);
    }

    /**
     * Returns a string containing the name of a {@link Player}. If the name is longer than {@link #nameLengthLimit},
     * a truncated version with ellipses is returned instead.
     * @param player the player.
     * @return the string.
     */
    static String nameOf(Player player) {
        String name = player.getName();
        if (name.length() <= nameLengthLimit)
            return name;
        return name.substring(0, nameLengthLimit - 4) + "...";
    }

    /**
     * Adds a coloured writing to the interface.
     * @param i the vertical position, from the top.
     * @param j the horizontal position, from the left.
     * @param message the string to write.
     */
    static void write(int i, int j, ColoredString message) {
        for(int k = 0; k < message.content().length(); k ++) {
            canvas[i][j + k] = new ColoredString(message.content().substring(k, k + 1), message.color());
        }
    }

    /**
     * Adds a series of coloured writings to the interface.
     * @param i the vertical position, from the top.
     * @param j the horizontal position, from the left.
     * @param message a collection of {@link ColoredString}s. These will be concatenated but each
     *                will retain its colour.
     */
    static void write(int i, int j, List<ColoredString> message) {
        int caret = 0;
        for(ColoredString cs : message) {
            for (int k = 0; k < cs.content().length(); k ++) {
                canvas[i][caret + j] = new ColoredString(cs.content().substring(k, k + 1), cs.color());
                caret ++;
            }
        }
    }

    /**
     * Builds a coloured rectangular frame on the interface.
     * @param i the vertical position, from the top, of the top-left corner of the frame.
     * @param j the horizontal position, from the left, of the top-left corner of the frame.
     * @param width the external width of the frame, expressed in characters.
     * @param height the external height of the frame, expressed in characters.
     * @param ansiColor the {@code ANSI} escape sequence of the colour to make the frame out of.
     */
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

    /**
     * Creates a grid of {@link ColoredString}s depicting the {@link Game} status from a {@link Player}'s perspective.
     * @param player the player.
     * @return the grid.
     */
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
