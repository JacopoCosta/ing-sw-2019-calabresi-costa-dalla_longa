package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.exceptions.AbortedTurnException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.virtual.VirtualView;

import java.util.ArrayList;
import java.util.List;

/**
 * Damage is an effect inflicted to a player that reduces their health and enables adrenaline actions above
 * certain thresholds.
 */
public class Damage extends OffensiveEffect {
    /**
     * This is the only constructor.
     * @param amount the amount of health points the damage takes.
     * @param constraints the requirements each target needs to meet in order to be affected.
     */
    Damage(int amount, List<Constraint> constraints) {
        this.amount = amount;
        this.constraints = constraints;
        this.type = EffectType.DAMAGE;
    }

    /**
     * Sets up a damage to be applied, by elaborating the valid target list and checking if the attacker
     * can use scopes to increase the damage amount to some of their targets.
     */
    @Override
    public void apply() {
        List<Player> targets = Constraint.filterPlayers(context, constraints);
        // as long as the author has (and wants to use) targeting scopes, they may do so;
        // this will increase the amount of damage dealt to any of its current targets

        if(targets.size() > 0) {
            VirtualView virtualView = author.getGame().getVirtualView();
            try {
                virtualView.scope(this, targets);
            } catch (AbortedTurnException ignored) {
                applyAfterScopes(targets, new ArrayList<>());// can't ask for scopes if they disconnected
            }
        }
    }

    /**
     * Effectively applies the damage taking into accounts the base targets and the targets on whom
     * the attacker also used one or more scopes.
     * @param targets the base targets - merely all those who meet all of the constraints.
     * @param scopedPlayers the scoped players.
     */
    public void applyAfterScopes(List<Player> targets, List<Player> scopedPlayers) {
        targets.forEach(target -> {
            int scopeCount = (int) scopedPlayers.stream()
                    .filter(sp -> sp.equals(target))
                    .count();
            int trueDamage = amount + scopeCount;
            for(int i = 0; i < trueDamage; i ++)
                target.applyDamage(author);
            VirtualView virtualView = author.getGame().getVirtualView();
            virtualView.announceDamage(author, target, trueDamage);
            if(target.isKilled())
                virtualView.announceKill(author, target);
        });

        VirtualView virtualView = author.getGame().getVirtualView();
        targets.stream()
                .filter(p -> p.getGrenades().size() > 0)
                .forEach(p -> {
                    try {
                        virtualView.grenade(p, author);
                    } catch (AbortedTurnException ignored) { } // can't ask for grenades if they disconnected
                });
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(amount + " damage to player such that");
        for(Constraint constraint : constraints)
            s.append(" (").append(constraint.toString()).append(")");
        return s.toString();
    }

}
