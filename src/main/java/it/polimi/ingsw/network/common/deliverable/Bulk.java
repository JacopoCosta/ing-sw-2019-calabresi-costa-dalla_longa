package it.polimi.ingsw.network.common.deliverable;

public class Bulk extends Deliverable {

    private Object object;

    public Bulk(DeliverableEvent event, Object object) {
        super(event);
        this.type = DeliverableType.BULK;
        this.object = object;
    }

    public Object unpack() {
        return object;
    }

}
