package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ScoreList;
import it.polimi.ingsw.util.printer.Color;
import it.polimi.ingsw.util.printer.ColoredString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;
import static it.polimi.ingsw.view.virtual.cli.CliCommon.canvas;

/**
 * This class is in charge of drawing the small status boards (also known as {@code Toaster}s) for each {@link Player}.
 */
abstract class CliToasters {
    /**
     * The top margin of the first toaster.
     */
    private static final int top = 37;

    /**
     * The left margin of the first toaster.
     */
    private static final int left = 0;

    /**
     * The width, in characters, of each toaster.
     */
    private static final int toasterWidth = 35;

    /**
     * The height, in characters, of each toaster.
     */
    private static final int toasterHeight = 10;

    /**
     * Depicts all the toasters, indicating which one belongs to a given {@link Player}.
     *
     * @param player the player.
     */
    static void build(Player player) {
        for (Player p : player.getGame().getParticipants())
            writeToaster(p);

        CliCommon.write(top - 1, 0, new ColoredString("Players:", Color.RESET));
        CliCommon.write(top + toasterHeight, (player.getId() - 1) * toasterWidth + (toasterWidth - 1) / 2 - 5, new ColoredString("^^^ YOU ^^^", Color.RESET));
    }

    /**
     * Draws a single toaster, belonging to a player.
     *
     * @param player the player.
     */
    private static void writeToaster(Player player) {
        int index = player.getId() - 1;
        String ansiColor = CliCommon.toAnsiColor(player);
        String name = CliCommon.nameOf(player);

        CliCommon.frame(top, left + index * toasterWidth, toasterWidth, toasterHeight, CliCommon.toAnsiColor(player));

        List<ColoredString> nameAndAmmo = new ArrayList<>();
        nameAndAmmo.add(new ColoredString(name, ansiColor));
        nameAndAmmo.add(new ColoredString(" ".repeat(CliCommon.nameLengthLimit + 2 - name.length()), null));
        nameAndAmmo.addAll(player.getAmmoCubes().toColoredStringsWithNumbers());
        writeOnToaster(index, 1, nameAndAmmo);

        List<ColoredString> markStrip = new ArrayList<>();
        markStrip.add(new ColoredString(" Marks:", Color.RESET));
        for (Player p : player.getMarkingsAsList())
            markStrip.add(new ColoredString(" " + full, CliCommon.toAnsiColor(p)));
        writeOnToaster(index, 3, markStrip);

        List<ColoredString> damageStrip = new ArrayList<>();
        damageStrip.add(new ColoredString("Damage:", Color.RESET));
        for (int i = 0; i <= 11; i++) {
            if (i < player.getDamageAsList().size())
                damageStrip.add(new ColoredString(" " + full, CliCommon.toAnsiColor(player.getDamageAsList().get(i))));
            else
                damageStrip.add(new ColoredString(" " + empty, Color.RESET));
        }
        writeOnToaster(index, 5, damageStrip);

        List<ColoredString> damageMeasure = Collections.singletonList(new ColoredString(" ".repeat(8) + "1  |" + " ".repeat(5) + "|" + " ".repeat(9) + "|K O", Color.RESET));
        writeOnToaster(index, 6, damageMeasure);

        int indent = player.isOnFrenzy() ? 15 : 13;
        int numberCount = player.isOnFrenzy() ? 4 : 6;
        List<ColoredString> deathTrack = new ArrayList<>();
        deathTrack.add(new ColoredString(" ".repeat(indent), null));
        for (int i = 0; i < numberCount; i++) {
            if (i >= player.getDeathCount())
                deathTrack.add(new ColoredString(" " + ScoreList.get(i, player.isOnFrenzy()), Color.RESET));
            else
                deathTrack.add(new ColoredString(" " + skull, Color.RED));
        }
        writeOnToaster(index, 8, deathTrack);
    }

    /**
     * Adds a series of coloured writings to the {@code Toaster}.
     *
     * @param index          the index of the {@code Toaster} to write on.
     * @param row            the number of the row at which to write, starting from the top.
     * @param coloredStrings a collection of {@link ColoredString}s. These will be concatenated but each
     *                       will retain its colour.
     */
    private static void writeOnToaster(int index, int row, List<ColoredString> coloredStrings) {
        int caret = 0;

        for (ColoredString cs : coloredStrings) {
            for (int i = 0; i < cs.content().length(); i++) {
                canvas[row + top][index * toasterWidth + 2 + caret + left] = new ColoredString(cs.content().substring(i, i + 1), cs.color());
                caret++;
            }
        }
    }
}
