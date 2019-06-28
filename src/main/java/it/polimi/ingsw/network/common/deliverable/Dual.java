package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.server.Server;

/**
 * {@code Dual}-type {@link Deliverable}s are used to convey requests whose answer is dual (i.e. a {@code boolean}).
 * Such requests are sent by the {@link Server} to query the {@link Client} about "yes-or-no" questions. The server
 * is blocked waiting until a {@link Response} is sent back by the client.
 */
public class Dual extends Deliverable {

    /**
     * This is the only constructor.
     * @param event the event that caused the {@code Dual} to be sent.
     */
    public Dual(DeliverableEvent event) {
        super(event);
        this.type = DeliverableType.DUAL;
    }
}
