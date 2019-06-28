package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.server.Server;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code Mapped}-type {@link Deliverable}s are sent by the {@link Server} to query the {@link Client} about
 * multiple-choice questions. The server is blocked waiting until a {@link Response} is sent back by the client.
 */
public class Mapped extends Deliverable{

    /**
     * A list containing the descriptions of the possible choices for the end user.
     */
    private List<String> options;

    /**
     * A list containing the numeric input required to select each of the {@link #options}.
     */
    private List<Integer> keys;

    /**
     * This constructor is used when a custom set of numbers make up the list of {@link #keys}.
     * @param event the event that caused the {@code Mapped} to be sent.
     * @param options the possible {@link #options} for the end user.
     * @param keys the desired set of {@link #keys} for the {@code options}.
     */
    public Mapped(DeliverableEvent event, List<String> options, List<Integer> keys) {
        super(event);
        this.type = DeliverableType.MAPPED;
        this.options = options;
        this.keys = keys;
    }

    /**
     * This constructor is used when the {@link #keys} should simply be the positive integers in ascending order.
     * @param event the event that caused the {@code Mapped} to be sent.
     * @param options the possible {@link #options} for the end user.
     */
    public Mapped(DeliverableEvent event, List<String> options) {
        super(event);
        this.type = DeliverableType.MAPPED;
        this.options = options;
        this.keys = options.stream()
                .map(options::indexOf)
                .map(x -> x + 1)
                .collect(Collectors.toList());
    }

    /**
     * Gets the {@link #options} of the {@code Mapped}.
     * @return the options.
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Gets the {@link #keys} of the {@code Mapped}.
     * @return the keys.
     */
    public List<Integer> getKeys() {
        return keys;
    }
}
