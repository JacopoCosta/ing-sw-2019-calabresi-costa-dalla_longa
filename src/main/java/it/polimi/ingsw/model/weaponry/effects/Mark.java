package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.virtual.VirtualView;

import java.util.List;

public class Mark extends OffensiveEffect{

    public Mark(int amount, List<Constraint> constraints) {
        this.amount = amount;
        this.constraints = constraints;
        this.type = EffectType.MARK;
    }

    @Override
    public void apply() {
        List<Player> targets = Constraint.filterPlayers(context, constraints);
        targets.forEach(target -> {
            for(int i = 0; i < amount; i ++)
                target.applyMarking(author);
            VirtualView virtualView = author.getGame().getVirtualView();
            virtualView.announceMarking(author, target, amount);
        });
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
