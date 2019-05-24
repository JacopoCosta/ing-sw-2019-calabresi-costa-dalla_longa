package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.powerups.PowerUpType;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.virtual.VirtualView;

import java.util.List;
import java.util.stream.Collectors;

public class Damage extends OffensiveEffect {
    public Damage(int amount, List<Constraint> constraints) {
        this.amount = amount;
        this.constraints = constraints;
        this.type = EffectType.DAMAGE;
    }

    @Override
    public void apply() {
        List<Player> targets = Constraint.filterPlayers(context, constraints);

        // as long as the author has (and wants to use) targeting scopes, they may do so;
        // this will increase the amount of damage dealt to any of its current targets
        List<PowerUp> scopes = author.getPowerUps()
                .stream()
                .filter(p -> p.getType() == PowerUpType.SCOPE)
                .collect(Collectors.toList());

        VirtualView virtualView = author.getGame().getVirtualView();
        virtualView.scope(this, scopes, targets);
    }

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
        });

        VirtualView virtualView = author.getGame().getVirtualView();
        targets.stream()
                .filter(p -> p.getGrenades().size() > 0)
                .forEach(p -> virtualView.grenade(p, p.getGrenades(), author));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(amount + " damage to player such that");
        for(Constraint constraint : constraints)
            s.append(" (").append(constraint.toString()).append(")");
        return s.toString();
    }

}
