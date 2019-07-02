package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.network.server.lobby.Lobby;

/**
 * Indicates that an attempt was made to create a new {@link Lobby} with the same identifier of another one already found
 * into the server.
 */
public class LobbyAlreadyExistsException extends Exception {
    /**
     * Constructs a new {@code LobbyAlreadyExistsException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public LobbyAlreadyExistsException(String s) {
        super(s);
    }
}