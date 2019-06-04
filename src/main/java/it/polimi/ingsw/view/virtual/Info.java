package it.polimi.ingsw.view.virtual;

public class Info extends Deliverable {
    protected Info(DeliverableEvent event) {
        super(event);
        this.type = DeliverableType.INFO;
    }
}
