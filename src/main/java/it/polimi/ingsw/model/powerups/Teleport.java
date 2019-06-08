package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;

/**
 * Teleports allow players to move to a new position, arbitrarily far away from their current one.
 */
public class Teleport extends PowerUp{
    /**
     * This is the only constructor.
     * @param ammoCubes a set of ammo cubes containing only one cube of the colour of the power up.
     */
    public Teleport(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.TELEPORT;
    }
}
