package it.polimi.ingsw.powerups;

import it.polimi.ingsw.ammo.AmmoCubes;
import it.polimi.ingsw.board.cell.Cell;
import it.polimi.ingsw.player.Player;

public abstract class PowerUp {
    protected AmmoCubes ammoCubes;
    protected Cell spawnPoint;

    public static PowerUp build(String s) {
        return null; //TODO PowerUp factory
    }

    public abstract void use(Player subject);
}
