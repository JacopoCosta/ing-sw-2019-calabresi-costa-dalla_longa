package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.util.printer.ColoredString;

/**
 * {@code Assets}-type {@link Deliverable}s are used to display with colour the {@link Game} status. They're always sent by
 * the {@link Server} and received by the {@link Client}. The server does not require (or indeed expect) a {@link Response}
 * after sending one. Not to be confused with {@link Info}, which is used to convey simple information to the user.
 */
public class Assets extends Deliverable {

    /**
     * A grid used to store all the {@link ColoredString}s needed to print the interface.
     */
    private ColoredString[][] matrix;

    /**
     * This is the only constructor.
     * @param event the event that caused the {@code Assets} to be sent.
     * @param matrix the value of the {@link #matrix}.
     */
    public Assets(DeliverableEvent event, ColoredString[][] matrix) {
        super(event);
        this.type = DeliverableType.ASSETS;
        this.matrix = matrix;
    }

    /**
     * Gets the {@link #matrix} attribute.
     * @return the {@link #matrix} attribute.
     */
    public ColoredString[][] unpack() {
        return matrix;
    }
}
