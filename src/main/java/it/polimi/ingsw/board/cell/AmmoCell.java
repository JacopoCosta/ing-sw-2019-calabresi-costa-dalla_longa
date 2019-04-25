package it.polimi.ingsw.board.cell;

import it.polimi.ingsw.ammo.AmmoTile;

public class AmmoCell extends Cell {
    private AmmoTile ammoTile;

    public AmmoCell(int xCoord, int yCoord) {
        super(xCoord, yCoord);
        this.spawnPoint = false;
    }

    public AmmoTile getAmmoTile() {
        return ammoTile;
    }

    public void setAmmoTile(AmmoTile ammoTile) {
        this.ammoTile = ammoTile;
    }
}