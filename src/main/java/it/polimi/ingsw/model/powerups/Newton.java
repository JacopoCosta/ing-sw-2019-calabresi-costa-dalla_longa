package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Player;

public class Newton extends PowerUp{
    private final static int NEWTON_MOVES = 2;

    public Newton(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.NEWTON;
    }

    @Override
    public void use(Player subject) {

    }
}
