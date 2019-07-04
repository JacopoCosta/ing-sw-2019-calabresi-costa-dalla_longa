package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.ColoredString;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;
import static it.polimi.ingsw.view.virtual.cli.CliCommon.canvas;

public abstract class CliTracks {
    private static final int top = 0;
    private static final int left = 8;

    private static final int trackWidth = 27;
    private static final int trackHeight = 3;

    public static void build(Game game) {
        List<Player> doubleKillers = game.getBoard().getDoubleKillers();

        writeKillerTrack(game.getBoard().getKillers(), game.getRoundsLeft());
        writeDoubleKillerTrack(doubleKillers);

        CliCommon.write(top + 1, 0, new ColoredString("Kills:", Color.ANSI_RESET));
    }

    private static void writeKillerTrack(List<Player> rawKillers, int roundsLeft) {
        CliCommon.frame(top, left, trackWidth, trackHeight, Color.ANSI_BLACK);

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
            canvas[top + 1][left + 2 + 3 * k] = new ColoredString(skull, Color.ANSI_WHITE);
        }

        for(int k = 8 - roundsLeft; k < 8; k ++) {
            canvas[top + 1][left + 2 + 3 * k] = new ColoredString(skull, Color.ANSI_RED);
        }

        for(int i = 0; i < killers.size(); i ++)
            writeOnKillerTrack(8 - killers.size() + i - roundsLeft, killers.get(i), overKill.get(i));
    }

    private static void writeDoubleKillerTrack(List<Player> doubleKillers) {
        CliCommon.frame(top, left + trackWidth, 3 + 2 * doubleKillers.size(), trackHeight, Color.ANSI_BLACK);
        for(int i = 0; i < doubleKillers.size(); i ++)
            writeOnDoubleKillerTrack(i, doubleKillers.get(i));

    }

    private static void writeOnKillerTrack(int index, Player player, boolean overKill) {
        int i = top + 1;
        int j = left + 2 + 3 * index;

        canvas[i][j] = new ColoredString(full, CliCommon.toAnsiColor(player));
        if(overKill)
            canvas[i][j + 1] = new ColoredString(full, CliCommon.toAnsiColor(player));
    }

    private static void writeOnDoubleKillerTrack(int index, Player player) {
        int i = top + 1;
        int j = left + 2 + 2 * index;

        canvas[i][j] = new ColoredString(full, CliCommon.toAnsiColor(player));
    }
}
