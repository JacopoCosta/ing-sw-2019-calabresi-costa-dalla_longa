package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.network.server.lobby.Lobby;

/**
 * Indicates that an attempt was made to access a {@link Lobby} that no longer exists and cannot be found into the server
 * collection of {@link Lobby}.
 */
public class LobbyNotFoundException extends Exception {
    /**
     * Constructs a new {@code LobbyNotFoundException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public LobbyNotFoundException(String s) {
        super(s);
    }
}
