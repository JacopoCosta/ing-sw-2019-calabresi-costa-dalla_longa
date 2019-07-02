package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.server.lobby.Lobby;

/**
 * Indicates that an attempt was made to log a {@link Player} out from an empty {@link Lobby}.
 */
public class LobbyEmptyException extends Exception {
    /**
     * Constructs a new {@code LobbyEmptyException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public LobbyEmptyException(String s) {
        super(s);
    }
}