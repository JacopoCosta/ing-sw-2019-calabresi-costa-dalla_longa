package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.network.server.lobby.Lobby;

/**
 * Indicates that an invalid password has been used to try to connect to a specified {@link Lobby}.
 */
public class InvalidPasswordException extends Exception {
    /**
     * Constructs a new {@code InvalidPasswordException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public InvalidPasswordException(String s) {
        super(s);
    }
}