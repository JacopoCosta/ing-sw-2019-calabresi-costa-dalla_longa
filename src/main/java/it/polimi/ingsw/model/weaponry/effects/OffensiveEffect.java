package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class OffensiveEffect extends Effect {
    protected int amount;
    protected Player author;
    protected List<Constraint> constraints;

    public void setAuthor(Player author) {
        this.author = author;
    }

    protected Stream<Player> getTargetStream() {
        List<List<Player>> targetTable = new ArrayList<>();

        for(Constraint constraint : constraints) {
            List<Player> targets = constraint.filterPlayers(context);
            targetTable.add(targets);
        }

        Stream<Predicate<Player>> spp = targetTable.stream()
                .map(list -> (list::contains));

        Predicate<Player> meetsAll = player -> spp
                .map(predicate -> predicate.test(player))
                .reduce(true, (a, b) -> a && b);

        return targetTable.stream()
                .map(Collection::stream)
                .flatMap(Function.identity())
                .distinct()
                .filter(meetsAll);
    }
}
