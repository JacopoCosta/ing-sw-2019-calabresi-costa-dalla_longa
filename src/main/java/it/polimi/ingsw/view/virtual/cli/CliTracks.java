package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.ColoredString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;

public abstract class CliTracks {
    private static final int trackWidth = 27;
    private static final int trackHeight = 3;

    private static ColoredString[][] grid;

    public static ColoredString[][] build(Game game) {
        List<Player> doubleKillers = game.getBoard().getDoubleKillers();
        grid = new ColoredString[trackHeight][trackWidth + 3 + 2 * doubleKillers.size()];

        writeKillerTrack(game.getBoard().getKillers(), game.getRoundsLeft());
        writeDoubleKillerTrack(doubleKillers);

        return grid;
    }

    private static void writeKillerTrack(List<Player> rawKillers, int roundsLeft) {
        for(int i = 1; i <= 4; i ++)
            buildBorderCounterclockwise(i, false, 0);

        List<Player> killers = new ArrayList<>();
        List<Boolean> overKill = new ArrayList<>();

        for(int i = 0; i < rawKillers.size(); i ++) {
            boolean tail = i == rawKillers.size() - 1;
            Player current = rawKillers.get(i);
            Player next = tail ? null : rawKillers.get(i + 1);

            if(current != null) {
                killers.add(current);
                overKill.add(next == null && !tail);
            }
        }

        if(roundsLeft < 0)
            roundsLeft = 0;

        for(int k = 0; k < 8 - roundsLeft; k ++) {
            grid[1][2 + 3 * k] = new ColoredString(skull, Color.ANSI_WHITE);
        }

        for(int k = 8 - roundsLeft; k < 8; k ++) {
            grid[1][2 + 3 * k] = new ColoredString(skull, Color.ANSI_RED);
        }

        for(int i = 0; i < killers.size(); i ++)
            writeOnKillerTrack(8 - killers.size() + i - roundsLeft, killers.get(i), overKill.get(i));
    }

    private static void writeDoubleKillerTrack(List<Player> doubleKillers) {
        for(int i = 1; i <= 4; i ++)
            buildBorderCounterclockwise(i, true, doubleKillers.size());

        for(int i = 0; i < doubleKillers.size(); i ++)
            writeOnDoubleKillerTrack(i, doubleKillers.get(i));

    }

    private static void writeOnKillerTrack(int index, Player player, boolean overKill) {
        int i = 1;
        int j = 2 + 3 * index;

        grid[i][j] = new ColoredString(full, CliCommon.toAnsiColor(player));
        if(overKill)
            grid[i][j + 1] = new ColoredString(full, CliCommon.toAnsiColor(player));
    }

    private static void writeOnDoubleKillerTrack(int index, Player player) {
        int i = 1;
        int j = 2 + 2 * index;

        grid[i][j] = new ColoredString(full, CliCommon.toAnsiColor(player));
    }

    private static void buildBorderCounterclockwise(int cornerId, boolean doubleKillerTrack, int doubleKillerCount) {
        int width = doubleKillerTrack ? 3 + 2 * doubleKillerCount : trackWidth;
        int base = doubleKillerTrack ? trackWidth : 0;

        int i = cornerId == 1 || cornerId == 2 ? (trackHeight - 1) : 0;
        int j = base + (cornerId == 1 || cornerId == 4 ? 0 : (width - 1));

        String startingCorner = Arrays.asList(corner1, corner2, corner3, corner4).get(cornerId - 1);
        String line = cornerId % 2 == 0 ? vertical : horizontal;

        int limit = cornerId % 2 == 0 ? trackHeight : width;

        int di = Arrays.asList(0, -1, 0, 1).get(cornerId - 1);
        int dj = Arrays.asList(1, 0, -1, 0).get(cornerId - 1);

        grid[i][j] = new ColoredString(startingCorner, Color.ANSI_BLACK);
        for(int k = 1; k < limit - 1; k ++) {
            i += di;
            j += dj;
            grid[i][j] = new ColoredString(line, Color.ANSI_BLACK);
        }
    }
}
