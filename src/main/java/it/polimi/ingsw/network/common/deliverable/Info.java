package it.polimi.ingsw.network.common.deliverable;

public class Info extends Deliverable {
    public Info(DeliverableEvent event) {
        super(event);
        this.type = DeliverableType.INFO;
    }
}
