package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.model.player.Player;

/**
 * Indicates that an attempt was made to log a {@link Player} into a lobby that has already reached its maximum amount
 * of {@link Player}s possible.
 */
public class LobbyFullException extends Exception {
    /**
     * Constructs a new {@code LobbyFullException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public LobbyFullException(String s) {
        super(s);
    }
}
