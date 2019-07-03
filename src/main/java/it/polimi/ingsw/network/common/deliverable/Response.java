package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.network.server.Server;

/**
 * {@code Response}-type {@link Deliverable}s are the only ones sent by the client to the {@link Server},
 * and carry with them an integer equivalent to the end user's choice.
 */
public class Response extends Deliverable {
    /**
     * The end user's response.
     */
    private int number;

    /**
     * This is the only constructor.
     * @param number the end user's response.
     */
    public Response(int number) {
        super(DeliverableEvent.RESPONSE);
        this.type = DeliverableType.RESPONSE;
        this.number = number;
    }

    /**
     * Gets the {@code Response}'s {@link #number}.
     * @return the number.
     */
    public int getNumber() {
        return number;
    }
}
