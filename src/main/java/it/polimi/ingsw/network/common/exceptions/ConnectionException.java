package it.polimi.ingsw.network.common.exceptions;

/**
 * Indicates that an error occurred at the network level. This is a generic exception used to encapsulate more
 * technology specific exceptions (i.e. Socket/RMI) or platform specific (i.e. Windows/Linux).
 */
public class ConnectionException extends Exception {
    /**
     * Constructs a new {@code ConnectionException} with {@code e} as its default throwable cause.
     *
     * @param e the default throwable cause.
     */
    public ConnectionException(Exception e) {
        super(e);
    }

    /**
     * Constructs a new {@code ConnectionException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public ConnectionException(String s) {
        super(s);
    }
}
