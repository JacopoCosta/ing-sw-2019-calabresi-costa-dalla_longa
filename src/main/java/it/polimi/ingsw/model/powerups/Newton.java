package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Move;

/**
 * Newtons allow players to {@link Move} opponents to a cell in their field of view, provided the distance covered by the
 * opponent does not exceed a set threshold.
 */
public class Newton extends PowerUp {

    /**
     * The maximum distance the target can be moved.
     */
    private static final int MAX_DISTANCE = 2;

    /**
     * This is the only constructor.
     * @param ammoCubes a set of {@link AmmoCubes} containing only one cube of the colour of the {@link PowerUp}.
     */
    public Newton(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.NEWTON;
    }

    /**
     * Returns the maximum distance the target can be moved.
     * @return the maximum distance.
     */
    public static int getMaxDistance() {
        return MAX_DISTANCE;
    }
}
