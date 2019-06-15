package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Move;

/**
 * Teleports allow players to {@link Move} to a new position, arbitrarily far away from their current one.
 */
public class Teleport extends PowerUp{
    /**
     * This is the only constructor.
     * @param ammoCubes a set of {@link AmmoCubes} containing only one cube of the colour of the {@link PowerUp}.
     */
    public Teleport(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.TELEPORT;
    }
}
