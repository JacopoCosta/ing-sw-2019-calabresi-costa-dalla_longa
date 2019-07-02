package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.network.server.lobby.Lobby;
import it.polimi.ingsw.model.player.Player;

/**
 * Indicates that an attempt was made to add an already logged {@link Player} to the same {@link Lobby} more than once.
 */
public class PlayerAlreadyAddedException extends Exception {
    /**
     * Constructs a new {@code PlayerAlreadyAddedException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public PlayerAlreadyAddedException(String s) {
        super(s);
    }
}
