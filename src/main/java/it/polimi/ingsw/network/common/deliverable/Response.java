package it.polimi.ingsw.network.common.deliverable;

public class Response extends Deliverable {
    private int number;

    public Response(int number) {
        super(DeliverableEvent.RESPONSE);
        this.type = DeliverableType.RESPONSE;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
