package it.polimi.ingsw.view.virtual;

public class Bulk extends Deliverable {

    private Object object;

    private String message;

    protected Bulk(DeliverableEvent event, Object object) {
        super(event);
        this.type = DeliverableType.BULK;
        this.object = object;
    }

    public Object unpack() {
        return object;
    }

}
