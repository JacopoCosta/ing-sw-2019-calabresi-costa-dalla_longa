package it.polimi.ingsw.powerups;

import it.polimi.ingsw.ammo.AmmoCubes;
import it.polimi.ingsw.board.cell.Cell;
import it.polimi.ingsw.player.Player;

public abstract class Scope extends PowerUp{

    public Scope(AmmoCubes ammoCubes, Cell spawnPoint) {
        this.ammoCubes = ammoCubes;
        this.spawnPoint = spawnPoint;
    }

    @Override
    public void use(Player subject) {
        subject.useScope();
    }
}
