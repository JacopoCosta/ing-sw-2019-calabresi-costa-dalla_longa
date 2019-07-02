package it.polimi.ingsw.network.common.exceptions;

/**
 * Indicates that an attempt to register a new client has failed because another client with the same identifier
 * has been found into the server.
 */
public class ClientAlreadyRegisteredException extends Exception {
    /**
     * Constructs a new {@code ClientAlreadyRegisteredException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public ClientAlreadyRegisteredException(String s) {
        super(s);
    }
}
