package it.polimi.ingsw.powerups;

import it.polimi.ingsw.ammo.AmmoCubes;
import it.polimi.ingsw.player.Player;

public class Scope extends PowerUp{

    public Scope(AmmoCubes ammoCubes) {
        super(ammoCubes);
    }

    @Override
    public void use(Player subject) {
        subject.useScope();
    }
}
