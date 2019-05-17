package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;

public class Grenade extends PowerUp {
    public Grenade(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.GRENADE;
    }
}
