package it.polimi.ingsw.view.virtual;

public class Deliverable {
    private String message;

    public Deliverable(String message) {
        this.message = message;
    }

    public Deliverable(Message message) {
        this.message = message.string;
    }

    public String unpack() {
        return this.message;
    }
}
