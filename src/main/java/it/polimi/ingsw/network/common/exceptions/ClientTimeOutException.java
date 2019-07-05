package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.network.client.executable.Client;

/**
 * Indicates that a {@link Client}'s time to answer expired before they gave the answer.
 */
public class ClientTimeOutException extends Exception {
    public ClientTimeOutException(String message) {
        super(message);
    }
}
