package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Player;

public class Grenade extends PowerUp {
    private static final int GRENADE_TAGBACK_MARKS = 1;

    public Grenade(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.GRENADE;
    }

    @Override
    public void use(Player subject) {

    }
}
