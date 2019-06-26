package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.Grenade;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.virtual.VirtualView;

import java.util.List;

/**
 * {@link Mark}s are {@link Effect}s inflicted to a {@link Player} that causes them to take increased {@link Damage} the next time they
 * are damaged by the same source as the {@link Mark}.
 */
public class Mark extends OffensiveEffect{

    /**
     * This is the only constructor.
     * @param amount the amount of {@link Mark}s the {@link Effect} applies.
     * @param constraints the list of requirements each {@link Player} needs to meet in order to be affected.
     */
    public Mark(int amount, List<Constraint> constraints) {
        this.amount = amount;
        this.constraints = constraints;
        this.type = EffectType.MARK;
    }

    /**
     * Sets up a {@link Mark} to be applied by elaborating the valid target list and then applies it directly to all
     * {@link Player}s in that list.
     */
    @Override
    public void apply() {
        List<Player> targets = Constraint.filterPlayers(context, constraints);
        targets.forEach(target -> {
            for(int i = 0; i < amount; i ++)
                target.applyMarking(author);
            VirtualView virtualView = author.getGame().getVirtualView();
            virtualView.sendUpdateMarking(target, author, amount);
        });
    }

    /**
     * This is a light-weight version of {@code apply()}, since it is used when a {@link Player} is hit by an attack
     * and wants to respond with a tagback {@link Grenade}, dealing 1 {@link Mark} to their original attacker.
     * @param author the {@link Player} being hit.
     * @param target the original attacker.
     */
    public void grenade(Player author, Player target) {
        target.applyMarking(author);
    }

    /**
     * Creates a short description of the effect.
     * @return the description.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(amount + " mark(s) to player such that");
        for(Constraint constraint : constraints)
            s.append(" (").append(constraint.toString()).append(")");
        return s.toString();
    }
}
