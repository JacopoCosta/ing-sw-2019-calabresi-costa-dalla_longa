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
     * Whether or not the {@link #number} is valid.
     */
    private boolean valid;

    /**
     * This is constructor produces a valid {@code Response} with the given number.
     * @param number the end user's response.
     */
    public Response(int number) {
        super(DeliverableEvent.RESPONSE);
        this.type = DeliverableType.RESPONSE;
        this.valid = true;
        this.number = number;
    }

    /**
     * This method creates a tainted {@code Response}, with its {@link #valid} flag set to {@code false}.
     * @return the {@code Response}.
     */
    public static Response taint() {
        Response response = new Response(0);
        response.valid = false;
        return response;
    }

    /**
     * Gets the {@code Response}'s {@link #number}.
     * @return the number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the {@code Response}'s {@link #valid} flag.
     * @return the flag's value.
     */
    public boolean isValid() {
        return valid;
    }
}
