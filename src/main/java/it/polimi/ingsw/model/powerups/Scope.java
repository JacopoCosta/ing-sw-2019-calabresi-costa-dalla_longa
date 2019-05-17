package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Player;

public class Scope extends PowerUp{
    public Scope(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.SCOPE;
    }
}
