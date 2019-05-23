package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.Table;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.remote.Dispatcher;

import java.util.List;
import java.util.stream.Collectors;

public class Mark extends OffensiveEffect{

    public Mark(int amount, List<Constraint> constraints) {
        this.amount = amount;
        this.constraints = constraints;
        this.type = EffectType.MARK;
    }

    @Override
    public void apply() {
        List<Player> targets = Constraint.filterPlayers(context, constraints);

        Dispatcher.sendMessage(author.getName() + " deals " + amount + " marks to " + Table.list(targets.stream()
                .map(Player::getName).collect(Collectors.toList())) + ".\n");

        for(int i = 0; i < amount; i ++)
            targets.forEach(p -> p.applyMarking(author));
    }

    public void grenade(Player author, Player target) {
        target.applyMarking(author);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(amount + " mark(s) to player such that");
        for(Constraint constraint : constraints)
            s.append(" (").append(constraint.toString()).append(")");
        return s.toString();
    }
}
