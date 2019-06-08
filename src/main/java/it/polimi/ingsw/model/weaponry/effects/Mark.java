package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.virtual.VirtualView;

import java.util.List;

/**
 * Marks are effects inflicted to a player that causes them to take increased damage the next time they
 * are damaged by the same source as the mark.
 */
public class Mark extends OffensiveEffect{

    /**
     * This is the only constructor.
     * @param amount the amount of marks the effect applies.
     * @param constraints the list of requirements each player needs to meet in order to be affected.
     */
    public Mark(int amount, List<Constraint> constraints) {
        this.amount = amount;
        this.constraints = constraints;
        this.type = EffectType.MARK;
    }

    /**
     * Sets up a marking to be applied by elaborating the valid target list and then applies it directly to all
     * players in that list.
     */
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

    /**
     * This is a light-weight version of {@code apply()}, since it is used when a player is hit by an attack
     * and wants to respond with a tagback grenade, dealing 1 mark to their original attacker.
     * @param author the player being hit.
     * @param target the original attacker.
     */
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
