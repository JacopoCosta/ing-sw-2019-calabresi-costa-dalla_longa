package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.server.Server;

/**
 * {@code Bulk}-type {@link Deliverable}s are used to convey generic objects, most often compound lists,
 * from the {@link Server} to the {@link Client}. Since these are primarily used to transport data,
 * unbeknownst to the player, no {@link Response} is required (or indeed expected) by the server, after sending a {@code Bulk}.
 */
//  ^ Footnote: IntelliJ's scrubby vocabulary thinks that "unbeknownst" is a typo, but it's not. It's a valid word.
public class Bulk extends Deliverable {

    /**
     * A generic field in which to put the body of the {@code Bulk} to be sent.
     */
    private Object object;

    /**
     * This is the only constructor.
     * @param event the event that caused the {@code Bulk} to be sent.
     * @param object the content of the {@code Bulk}.
     */
    public Bulk(DeliverableEvent event, Object object) {
        super(event);
        this.type = DeliverableType.BULK;
        this.object = object;
    }

    /**
     * Gets the {@code Bulk}'s {@link #object}
     * @return the object.
     */
    public Object unpack() {
        return object;
    }

}
