package it.polimi.ingsw.board.cell;

import it.polimi.ingsw.ammo.AmmoCubes;
import it.polimi.ingsw.weaponry.Weapon;

import java.util.List;

public class SpawnCell extends Cell {
    private AmmoCubes ammoCubeColor; // this attribute is used to determine which Cell a given PowerUp refers to as its spawnPoint
    private List<Weapon> weaponShop;

    public SpawnCell(int xCoord, int yCoord, AmmoCubes ammoCubeColor) {
        super(xCoord, yCoord);
        this.ammoCubeColor = ammoCubeColor;
        this.spawnPoint = true;
    }

    public AmmoCubes getAmmoCubeColor() {
        return this.ammoCubeColor;
    }
}