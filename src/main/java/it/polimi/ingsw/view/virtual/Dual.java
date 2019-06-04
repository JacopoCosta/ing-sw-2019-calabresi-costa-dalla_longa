package it.polimi.ingsw.view.virtual;

public class Dual extends Deliverable {
    protected Dual(DeliverableEvent event) {
        super(event);
        this.type = DeliverableType.DUAL;
    }
}
