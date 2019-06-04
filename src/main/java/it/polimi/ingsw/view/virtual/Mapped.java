package it.polimi.ingsw.view.virtual;

import java.util.List;
import java.util.stream.Collectors;

public class Mapped extends Deliverable{
    private List<String> options;
    private List<Integer> keys;

    protected Mapped(DeliverableEvent event, List<String> options, List<Integer> keys) {
        super(event);
        this.type = DeliverableType.MAPPED;
        this.options = options;
        this.keys = keys;
    }

    protected Mapped(DeliverableEvent event, List<String> options) {
        super(event);
        this.type = DeliverableType.MAPPED;
        this.options = options;
        this.keys = options.stream()
                .map(options::indexOf)
                .map(x -> x + 1)
                .collect(Collectors.toList());
    }

    public List<String> getOptions() {
        return options;
    }

    public List<Integer> getKeys() {
        return keys;
    }
}
