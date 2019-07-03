package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.server.Server;

/**
 * {@code Info}-type {@link Deliverable}s are used to convey simple information to the user. They're always sent by
 * the {@link Server} and received by the {@link Client}. The server does not require (or indeed expect) a {@link Response}
 * after sending one. Not to be confused with {@link Assets}, which brings information to the client, but not to the end-user.
 */
public class Info extends Deliverable {

    /**
     * This is the only constructor.
     * @param event the event that caused the {@code Info} to be sent.
     */
    public Info(DeliverableEvent event) {
        super(event);
        this.type = DeliverableType.INFO;
    }
}
