package it.polimi.ingsw.network.common.util.property;

import it.polimi.ingsw.model.Game;

/**
 * This class contains all the properties needed in order to start a new {@link Game}. These properties can't
 * simply be coded into the {@link Game} logic, as they need to be changed between different executions.
 *
 * @see Game
 */
public class GameProperty {
    /**
     * The {@code GameProperty} representing whether or not the {@link Game} should start with the final frenzy
     * property, as described in the {@link Game} {@code finalFrenzy} flag.
     */
    private final boolean finalFrenzy;

    /**
     * The {@code GameProperty} representing the number of rounds a {@link Game} should last,
     * as described in the {@link Game} {@code roundsLeft} attribute.
     */
    private final int roundsToPlay;

    /**
     * The {@code GameProperty} representing the type of the board a {@link Game} should load,
     * as described in the {@link Game} {@code boardType} attribute.
     */
    private final int boardType;

    /**
     * The {@code GameProperty} representing the maximum amount of time in seconds a {@link Game} turn can last,
     * as described in the {@link Game} //TODO attribute.
     */
    private final int turnDuration;

    /**
     * This is the only constructor. It creates a new {@code GameProperty} with the given property values
     *
     * @param finalFrenzy  the {@link Game} {@code finalFrenzy} flag.
     * @param roundsToPlay the {@link Game} {@code roundsToPlay} attribute.
     * @param boardType    the {@link Game} {@code boardType} attribute.
     * @param turnDuration the {@link Game} {@code turnDuration} attribute.
     */
    GameProperty(boolean finalFrenzy, int roundsToPlay, int boardType, int turnDuration) {
        this.finalFrenzy = finalFrenzy;
        this.roundsToPlay = roundsToPlay;
        this.boardType = boardType;
        this.turnDuration = turnDuration;
    }

    /**
     * Returns the {@link #finalFrenzy} current value.
     *
     * @return the {@link #finalFrenzy} current value.
     */
    public boolean finalFrenzy() {
        return this.finalFrenzy;
    }

    /**
     * Returns the {@link #roundsToPlay} current value.
     *
     * @return the {@link #roundsToPlay} current value.
     */
    public int roundsToPlay() {
        return this.roundsToPlay;
    }

    /**
     * Returns the {@link #boardType current value.
     *
     * @return the {@link #boardType current value.
     */
    public int boardType() {
        return this.boardType;
    }

    /**
     * Returns the {@link #turnDuration} current value.
     *
     * @return the {@link #turnDuration} current value.
     */
    public int turnDuration() {
        return this.turnDuration;
    }
}
