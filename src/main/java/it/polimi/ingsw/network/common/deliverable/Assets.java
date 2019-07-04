package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.util.printer.ColoredString;

public class Assets extends Deliverable {

    private ColoredString[][] matrix;

    public Assets(DeliverableEvent event, ColoredString[][] matrix) {
        super(event);
        this.type = DeliverableType.ASSETS;
        this.matrix = matrix;
    }

    public ColoredString[][] unpack() {
        return matrix;
    }
}
