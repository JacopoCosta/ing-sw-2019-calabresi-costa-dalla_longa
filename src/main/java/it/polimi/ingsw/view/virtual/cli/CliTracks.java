package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.printer.Color;
import it.polimi.ingsw.util.printer.ColoredString;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;
import static it.polimi.ingsw.view.virtual.cli.CliCommon.canvas;

/**
 * This class is used for drawing the killshot track and double-kill track of a {@link Game}.
 */
public abstract class CliTracks {
    /**
     * The top margin of the tracks.
     */
    private static final int top = 0;

    /**
     * The left margin of the leftmost tracks.
     */
    private static final int left = 8;

    /**
     * The width of the killshot track, in characters.
     */
    private static final int trackWidth = 27;

    /**
     * The height of the tracks.
     */
    private static final int trackHeight = 3;

    /**
     * Draws the two tracks on {@link CliCommon}'s grid.
     * @param game the {@link Game} described by the tracks.
     */
    public static void build(Game game) {
        List<Player> doubleKillers = game.getBoard().getDoubleKillers();

        writeKillerTrack(game.getBoard().getKillers(), game.getRoundsLeft());
        writeDoubleKillerTrack(doubleKillers);

        CliCommon.write(top + 1, 0, new ColoredString("Kills:", Color.RESET));
    }

    /**
     * Draws the killshot track.
     * @param rawKillers a list containing the {@link Player} who scored kills. The value {@code null} after a {@link Player}
     *                   indicates an overkill by that player.
     * @param roundsLeft the number of rounds left to play in the {@link Game}.
     */
    private static void writeKillerTrack(List<Player> rawKillers, int roundsLeft) {
        CliCommon.frame(top, left, trackWidth, trackHeight, Color.BLACK);

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
            canvas[top + 1][left + 2 + 3 * k] = new ColoredString(skull, Color.WHITE);
        }

        for(int k = 8 - roundsLeft; k < 8; k ++) {
            canvas[top + 1][left + 2 + 3 * k] = new ColoredString(skull, Color.RED);
        }

        for(int i = 0; i < killers.size(); i ++)
            writeOnKillerTrack(8 - killers.size() + i - roundsLeft, killers.get(i), overKill.get(i));
    }

    /**
     * Draws the double-killer track.
     * @param doubleKillers a list containing all the {@link Player}s who scored a double-kill.
     */
    private static void writeDoubleKillerTrack(List<Player> doubleKillers) {
        CliCommon.frame(top, left + trackWidth, 3 + 2 * doubleKillers.size(), trackHeight, Color.BLACK);
        for(int i = 0; i < doubleKillers.size(); i ++)
            writeOnDoubleKillerTrack(i, doubleKillers.get(i));

    }

    /**
     * Adds a {@link Player}'s token to the killshot track.
     * @param index the position in which to put the token.
     * @param player the {@link Player} the token represents.
     * @param overKill whether or not a double token should be used instead.
     */
    private static void writeOnKillerTrack(int index, Player player, boolean overKill) {
        int i = top + 1;
        int j = left + 2 + 3 * index;

        canvas[i][j] = new ColoredString(full, CliCommon.toAnsiColor(player));
        if(overKill)
            canvas[i][j + 1] = new ColoredString(full, CliCommon.toAnsiColor(player));
    }

    /**
     * Adds a {@link Player}'s token to the double-kill track.
     * @param index the position in which to put the token.
     * @param player the {@link Player} the token represents.
     */
    private static void writeOnDoubleKillerTrack(int index, Player player) {
        int i = top + 1;
        int j = left + 2 + 2 * index;

        canvas[i][j] = new ColoredString(full, CliCommon.toAnsiColor(player));
    }
}
