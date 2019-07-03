package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.util.ColoredString;

/**
 * {@code Bulk}-type {@link Deliverable}s are used to convey generic objects, most often compound lists,
 * from the {@link Server} to the {@link Client}. Since these are primarily used to transport data,
 * unbeknownst to the player, no {@link Response} is required (or indeed expected) by the server, after sending a {@code Bulk}.
 */
//  ^ Footnote: IntelliJ's scrubby vocabulary thinks that "unbeknownst" is a typo, but it's not. It's a valid word.
public class Assets extends Deliverable {

    /**
     * A field in which to put the body of the {@code Assets} to be sent.
     */
    private ColoredString[][] graphicalMatrix;

    /**
     * This is the only constructor.
     * @param graphicalMatrix the content of the {@code Assets}.
     */
    public Assets(ColoredString[][] graphicalMatrix) {
        super(DeliverableEvent.UPDATE_VIEW);
        this.type = DeliverableType.ASSETS;
        this.graphicalMatrix = graphicalMatrix;
    }

    /**
     * Gets the {@code Assets}'s {@link #graphicalMatrix}
     * @return the object.
     */
    public ColoredString[][] unpack() {
        return graphicalMatrix;
    }

}
