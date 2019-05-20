package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.Table;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.Dispatcher;

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

    @Override
    public String toString() {
        String s = amount + " mark(s) to player such that";
        for(Constraint constraint : constraints)
            s += " (" + constraint.toString() + ")";
        return s;
    }
}
