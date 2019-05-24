package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Player;

public class Newton extends PowerUp {

    public static final int NEWTON_MAX_DISTANCE = 2;

    public Newton(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.NEWTON;
    }
}
