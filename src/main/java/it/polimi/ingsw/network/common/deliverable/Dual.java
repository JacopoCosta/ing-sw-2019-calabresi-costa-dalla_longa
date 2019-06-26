package it.polimi.ingsw.network.common.deliverable;

/**
 *
 */
public class Dual extends Deliverable {
    public Dual(DeliverableEvent event) {
        super(event);
        this.type = DeliverableType.DUAL;
    }
}
