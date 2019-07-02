package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.model.player.Player;

/**
 * Indicates that an attempt was made to refer to a {@link Player} that no longer exists on the server.
 */
public class PlayerNotFoundException extends Exception {
    /**
     * Constructs a new {@code PlayerNotFoundException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public PlayerNotFoundException(String s) {
        super(s);
    }
}
