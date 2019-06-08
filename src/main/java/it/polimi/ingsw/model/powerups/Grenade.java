package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;

/**
 * Grenades allow a player to respond to an attack by dealing 1 mark to the attacker.
 */
public class Grenade extends PowerUp {
    /**
     * This is the only constructor.
     * @param ammoCubes a set of ammo cubes containing only one cube of the colour of the power up.
     */
    public Grenade(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.GRENADE;
    }
}
