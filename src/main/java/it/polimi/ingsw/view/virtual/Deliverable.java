package it.polimi.ingsw.view.virtual;

public class Deliverable {
    private DeliverableType type;
    private String message;
    private int content;

    public Deliverable(String message) {
        this.type = DeliverableType.GENERIC;
        this.message = message;
        this.content = 0;
    }

    public Deliverable(DeliverableType type) {
        this.type = type;
        this.message = type.message;
        this.content = 0;
    }

    public void pack(int content) {
        this.content = content;
    }

    public DeliverableType getType() {
        return this.type;
    }

    public String getMessage() {
        return message;
    }

    public int unpack() {
        return content;
    }
}
