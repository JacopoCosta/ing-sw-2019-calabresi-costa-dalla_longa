package it.polimi.ingsw.view.virtual;

import java.util.List;

public class Deliverable {
    private static final int DEFAULT = -1;

    private DeliverableType type;
    private DeliverableEvent event;
    private String message;
    private List<String> options;
    private List<Integer> keys;
    private int number;

    static Deliverable info(DeliverableEvent event) {
        Deliverable deliverable = new Deliverable(DEFAULT);

        deliverable.type = DeliverableType.INFO;
        deliverable.event = event;
        deliverable.message = event.message;
        deliverable.options = null;
        deliverable.keys = null;

        return deliverable;
    }

    static Deliverable dual(DeliverableEvent event) {
        Deliverable deliverable = new Deliverable(DEFAULT);

        deliverable.type = DeliverableType.DUAL;
        deliverable.event = event;
        deliverable.message = event.message;
        deliverable.options = null;
        deliverable.keys = null;

        return deliverable;
    }

    static Deliverable listed(DeliverableEvent event, List<String> options) {
        Deliverable deliverable = new Deliverable(DEFAULT);

        deliverable.type = DeliverableType.LISTED;
        deliverable.event = event;
        deliverable.message = event.message;
        deliverable.options = options;
        deliverable.keys = null;

        return deliverable;
    }

    static Deliverable mapped(DeliverableEvent event, List<String> options, List<Integer> keys) {
        Deliverable deliverable = new Deliverable(DEFAULT);

        deliverable.type = DeliverableType.MAPPED;
        deliverable.event = event;
        deliverable.message = event.message;
        deliverable.options = options;
        deliverable.keys = keys;

        return deliverable;
    }

    public Deliverable(int content) {
        this.number = content;
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

    public List<String> getOptions() {
        return options;
    }

    public List<Integer> getKeys() {
        return keys;
    }

    int unpack() {
        return number;
    }
}
