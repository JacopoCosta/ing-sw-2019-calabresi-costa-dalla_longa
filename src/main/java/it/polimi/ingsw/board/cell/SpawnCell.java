package it.polimi.ingsw.board.cell;

import it.polimi.ingsw.weaponry.Weapon;

import java.util.List;

public class SpawnCell extends Cell {
    private List<Weapon> weaponShop;

    public SpawnCell(int xCoord, int yCoord) {
        super(xCoord, yCoord);
    }
}