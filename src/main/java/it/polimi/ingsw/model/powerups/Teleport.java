package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;

public class Teleport extends PowerUp{
    public Teleport(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.TELEPORT;
    }
}
