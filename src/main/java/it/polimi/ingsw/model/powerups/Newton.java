package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;

public class Newton extends PowerUp {

    private static final int MAX_DISTANCE = 2;

    public Newton(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.NEWTON;
    }

    public static int getMaxDistance() {
        return MAX_DISTANCE;
    }
}
