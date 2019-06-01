package it.polimi.ingsw.view.virtual;

public class Deliverable {
    private DeliverableType type;
    private String message;
    private Object content;

    public Deliverable(String message) {
        this.type = DeliverableType.GENERIC;
        this.message = message;
        this.content = null;
    }

    public Deliverable(DeliverableType type) {
        this.type = type;
        this.message = type.message;
        this.content = null;
    }

    public void pack(Object content) {
        this.content = content;
    }

    public DeliverableType getType() {
        return this.type;
    }

    public String getMessage() {
        return message;
    }

    public Object unpack() {
        return content;
    }
}
