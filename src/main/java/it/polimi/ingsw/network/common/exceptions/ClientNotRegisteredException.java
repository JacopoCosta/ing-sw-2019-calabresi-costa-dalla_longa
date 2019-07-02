package it.polimi.ingsw.network.common.exceptions;

/**
 * Indicates that an attempt to refer to an unregistered client was made.
 */
public class ClientNotRegisteredException extends Exception {
    /**
     * Constructs a new {@code ClientNotRegisteredException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public ClientNotRegisteredException(String s) {
        super(s);
    }
}
