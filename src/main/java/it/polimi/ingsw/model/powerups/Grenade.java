package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.weaponry.effects.Mark;

/**
 * Grenades allow a player to respond to an attack by dealing 1 {@link Mark} to the attacker.
 */
public class Grenade extends PowerUp {
    /**
     * This is the only constructor.
     *
     * @param ammoCubes a set of {@link AmmoCubes} containing only one cube of the colour of the {@link PowerUp}.
     */
    public Grenade(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.GRENADE;
    }
}
