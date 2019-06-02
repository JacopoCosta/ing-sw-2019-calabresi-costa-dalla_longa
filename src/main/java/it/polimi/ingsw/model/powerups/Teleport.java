package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;

public class Teleport extends PowerUp{
    public Teleport(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.TELEPORT;
    }
}
