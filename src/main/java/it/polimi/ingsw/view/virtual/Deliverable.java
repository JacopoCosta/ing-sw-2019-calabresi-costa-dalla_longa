package it.polimi.ingsw.view.virtual;

import it.polimi.ingsw.model.exceptions.DeliverableException;

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
        if(options.size() == 0)
            throw new DeliverableException("Can't create a listed deliverable with an empty options list.");

        Deliverable deliverable = new Deliverable(DEFAULT);

        deliverable.type = DeliverableType.LISTED;
        deliverable.event = event;
        deliverable.message = event.message;
        deliverable.options = options;
        deliverable.keys = null;

        return deliverable;
    }

    static Deliverable mapped(DeliverableEvent event, List<String> options, List<Integer> keys) {
        if(options.size() == 0)
            throw new DeliverableException("Can't create a mapped deliverable with an empty options list.");
        if(options.size() != keys.size())
            throw new DeliverableException("Can't create a mapped deliverable having options list and keys list of different lengths.");

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
