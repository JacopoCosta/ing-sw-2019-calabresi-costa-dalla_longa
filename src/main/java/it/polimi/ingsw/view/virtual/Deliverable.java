package it.polimi.ingsw.view.virtual;

import java.util.List;

public class Deliverable {
    private DeliverableType type;
    private String message;
    private List<?> details;

    public Deliverable(String message) {
        this.type = DeliverableType.GENERIC;
        this.message = message;
        this.details = null;
    }

    public Deliverable(DeliverableType type) {
        this.type = type;
        this.message = type.message;
        this.details = null;
    }

    public void addDetails(List<?> details) {
        this.details = details;
    }

    public DeliverableType getType() {
        return this.type;
    }

    public String getMessage() {
        return message;
    }

    public List<?> getDetails() {
        return details;
    }
}
