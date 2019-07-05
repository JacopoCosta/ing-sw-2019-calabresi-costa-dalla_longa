package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.weaponry.effects.Damage;

/**
 * {@link Scope}s allow an attacker to deal one additional {@link Damage} to one of their targets for each {@link Scope} used.
 * Using a {@link Scope} requires also paying a cost equal to one {@link AmmoCubes} of any colour.
 */
public class Scope extends PowerUp {
    /**
     * This is the only constructor.
     *
     * @param ammoCubes a set of {@link AmmoCubes} containing only one cube of the colour of the {@link PowerUp}.
     */
    public Scope(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.SCOPE;
    }
}
