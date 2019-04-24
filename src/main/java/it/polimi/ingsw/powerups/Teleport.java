package it.polimi.ingsw.powerups;

import it.polimi.ingsw.ammo.AmmoCubes;
import it.polimi.ingsw.board.cell.Cell;
import it.polimi.ingsw.player.Player;

public class Teleport extends PowerUp{
    private Cell destination;

    public Teleport(AmmoCubes ammoCubes) {
        super(ammoCubes);
    }

    public void setDestination(Cell destination) {
        this.destination = destination;
    }

    @Override
    public void use(Player subject) {
        subject.setPosition(destination);
    }
}
