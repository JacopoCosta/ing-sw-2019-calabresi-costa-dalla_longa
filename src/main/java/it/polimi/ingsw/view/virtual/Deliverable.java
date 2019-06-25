package it.polimi.ingsw.view.virtual;

import java.io.Serializable;

public abstract class Deliverable implements Serializable {
    protected DeliverableType type;
    protected DeliverableEvent event;
    protected String message;

    protected Deliverable(DeliverableEvent event) {
        this.event = event;
        this.message = event.message;
    }

    public void overwriteMessage(String message) {
        this.message = message;
    }

    public DeliverableType getType() {
        return type;
    }

    public DeliverableEvent getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }
}
