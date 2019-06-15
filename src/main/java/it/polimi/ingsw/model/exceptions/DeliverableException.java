package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.view.virtual.Deliverable;

/**
 * This exception is thrown when attempting to broadcast to all {@link Client} a {@link Deliverable} of a type that expects a response.
 * Broadcasts are only designed to support one-way communications from the server to all {@link Client}s. It is a runtime exception
 * because attempting such action implies a flaw in the MVC dispatching logic and in normal conditions such scenario should
 * never happen anyway.
 */
public class DeliverableException extends RuntimeException {
    public DeliverableException(String message) {
        super(message);
    }
}
