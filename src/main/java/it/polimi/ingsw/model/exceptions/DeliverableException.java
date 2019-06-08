package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to broadcast to all clients a Deliverable of a type that expects a response.
 * Broadcasts are only designed to support one-way communications from the server to all clients. It is a runtime exception
 * because attempting such action implies a flaw in the MVC dispatching logic and in normal conditions such scenario should
 * never happen anyway.
 * @see it.polimi.ingsw.view.virtual.Deliverable
 * @see it.polimi.ingsw.view.virtual.DeliverableType
 * @see it.polimi.ingsw.view.virtual.VirtualView
 */
public class DeliverableException extends RuntimeException {
    public DeliverableException(String message) {
        super(message);
    }
}
