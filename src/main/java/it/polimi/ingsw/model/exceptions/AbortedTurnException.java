package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.client.executable.Client;

/**
 * This exception is thrown by all methods that are transitively called by the method {@code playTurn()} of the {@link Game} class,
 * that are also responsible of opening a send/receive communication routine to one or more {@link Client}s. In case of lost connection,
 * this exception causes a premature ending of the current {@link Player}'s turn and allows to notify everyone else about what happened and
 * move on to the next {@link Player}.
 *
 * @see Game
 */
public class AbortedTurnException extends Exception {
    public AbortedTurnException(String message) {
        super(message);
    }
}
