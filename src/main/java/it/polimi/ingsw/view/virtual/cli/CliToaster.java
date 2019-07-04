package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ScoreList;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.ColoredString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;

public abstract class CliToaster {
    private static final int toasterWidth = 35;
    private static final int toasterHeight = 10;

    private static ColoredString[][] toasterField;

    public static ColoredString[][] build(Game game) {
        toasterField = new ColoredString[toasterHeight][toasterWidth * game.getParticipants().size()];

        int index = 0;
        for(Player p : game.getParticipants()) {
            writeToaster(index, p);
            index ++;
        }

        return toasterField;
    }

    private static void writeToaster(int index, Player player) {
        String ansiColor = CliCommon.toAnsiColor(player);
        String name = CliCommon.nameOf(player);

        for(int i = 1; i <= 4; i ++)
            buildBorderCounterclockwise(i, index, ansiColor);

        List<ColoredString> nameAndAmmo = new ArrayList<>();
        nameAndAmmo.add(new ColoredString(name, ansiColor));
        nameAndAmmo.add(new ColoredString(" ".repeat(CliCommon.nameLengthLimit + 2 - name.length()), null));
        nameAndAmmo.addAll(player.getAmmoCubes().toColoredStringsWithNumbers());
        writeOnToaster(index, 1, nameAndAmmo);

        List<ColoredString> markStrip = new ArrayList<>();
        markStrip.add(new ColoredString(" Marks:", Color.ANSI_RESET));
        for(Player p : player.getMarkingsAsList())
            markStrip.add(new ColoredString(" " + full, CliCommon.toAnsiColor(p)));
        writeOnToaster(index, 3, markStrip);

        List<ColoredString> damageStrip = new ArrayList<>();
        damageStrip.add(new ColoredString("Damage:", Color.ANSI_RESET));
        for(int i = 0; i <= 11; i ++) {
            if(i < player.getDamageAsList().size())
                damageStrip.add(new ColoredString(" " + full, CliCommon.toAnsiColor(player.getDamageAsList().get(i))));
            else
                damageStrip.add(new ColoredString(" " + empty, Color.ANSI_RESET));
        }
        writeOnToaster(index, 5, damageStrip);

        List<ColoredString> damageMeasure = Arrays.asList(new ColoredString(" ".repeat(8) + "1  |" + " ".repeat(5) + "|" + " ".repeat(9) + "|K O", Color.ANSI_RESET));
        writeOnToaster(index, 6, damageMeasure);

        int indent = player.isOnFrenzy() ? 15 : 13;
        int numberCount = player.isOnFrenzy() ? 4 : 6;
        List<ColoredString> deathTrack = new ArrayList<>();
        deathTrack.add(new ColoredString(" ".repeat(indent), null));
        for(int i = 0; i < numberCount; i ++) {
            if(i >= player.getDeathCount())
                deathTrack.add(new ColoredString(" " + ScoreList.get(i, player.isOnFrenzy()), Color.ANSI_RESET));
            else
                deathTrack.add(new ColoredString(" " + skull, Color.ANSI_RED));
        }
        writeOnToaster(index, 8, deathTrack);
    }

    private static void writeOnToaster(int index, int row, List<ColoredString> coloredStrings) {
        int caret = 0;

        for(ColoredString cs : coloredStrings) {
            for (int i = 0; i < cs.content().length(); i++) {
                toasterField[row][index * toasterWidth + 2 + caret] = new ColoredString(cs.content().substring(i, i + 1), cs.color());
                caret ++;
            }
        }
    }

    private static void buildBorderCounterclockwise(int cornerId, int index, String ansiColor) {
        int i = cornerId == 1 || cornerId == 2 ? (toasterHeight - 1) : 0;
        int j = index * toasterWidth + (cornerId == 1 || cornerId == 4 ? 0 : toasterWidth - 1);

        String startingCorner = Arrays.asList(corner1, corner2, corner3, corner4).get(cornerId - 1);
        String line = cornerId % 2 == 0 ? vertical : horizontal;

        int limit = cornerId % 2 == 0 ? toasterHeight : toasterWidth;

        int di = Arrays.asList(0, -1, 0, 1).get(cornerId - 1);
        int dj = Arrays.asList(1, 0, -1, 0).get(cornerId - 1);

        toasterField[i][j] = new ColoredString(startingCorner, ansiColor);
        for(int k = 1; k < limit - 1; k ++) {
            i += di;
            j += dj;
            toasterField[i][j] = new ColoredString(line, ansiColor);
        }
    }
}
