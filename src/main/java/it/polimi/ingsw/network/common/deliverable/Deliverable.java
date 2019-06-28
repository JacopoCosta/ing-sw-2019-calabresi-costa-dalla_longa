package it.polimi.ingsw.network.common.deliverable;

import java.io.Serializable;

/**
 * A {@code Deliverable} is a serializable data structure used to wrap and send chunks of information
 * across the net.
 */
public abstract class Deliverable implements Serializable {
    /**
     * The type of the {@code Deliverable}. This is used to define the
     * behaviour the receiver is expected have with the received {@code Deliverable}.
     */
    protected DeliverableType type;

    /**
     * The event that caused the {@code Deliverable} to be sent in the first place.
     */
    protected DeliverableEvent event;

    /**
     * The body of the deliverable, often containing a request to the end user.
     */
    protected String message;

    /**
     * This is the only constructor.
     * @param event the {@link #event} that caused the {@code Deliverable} to be sent.
     */
    public Deliverable(DeliverableEvent event) {
        this.event = event;
        this.message = event.message;
    }

    /**
     * Overwrites the {@code Deliverable}'s {@link #message} with a string passed as argument.
     * @param message the string.
     */
    public void overwriteMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the {@code Deliverable}'s {@link #type}.
     * @return the type.
     */
    public DeliverableType getType() {
        return type;
    }

    /**
     * Gets the {@code Deliverable}'s {@link #event}.
     * @return the event.
     */
    public DeliverableEvent getEvent() {
        return event;
    }

    /**
     * Gets the {@code Deliverable}'s {@link #message}.
     * @return the message.
     */
    public String getMessage() {
        return message;
    }
}
