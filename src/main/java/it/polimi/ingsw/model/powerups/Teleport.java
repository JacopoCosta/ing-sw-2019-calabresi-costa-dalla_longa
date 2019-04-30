package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;

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
