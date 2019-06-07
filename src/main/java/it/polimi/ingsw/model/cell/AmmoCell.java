package it.polimi.ingsw.model.cell;

import it.polimi.ingsw.model.ammo.AmmoTile;

/**
 * Ammo cells are cells that are able to host an ammo tile on them.
 */
public class AmmoCell extends Cell {

    /**
     * The ammo tile hosted on the cell. It is null when the cell is empty.
     */
    private AmmoTile ammoTile;

    /**
     * This is the only constructor.
     * @param xCoord the x (horizontal) coordinate in the 2-dimensional discrete space this cell will be put at.
     * @param yCoord the y (vertical) coordinate in the 2-dimensional discrete space this cell will be put at.
     */
    public AmmoCell(int xCoord, int yCoord) {
        super(xCoord, yCoord);
        this.spawnPoint = false;
    }

    /**
     * Returns the ammo tile hosted on the cell.
     * @return the ammo tile hosted on the cell.
     */
    public AmmoTile getAmmoTile() {
        return ammoTile;
    }

    /**
     * Sets the ammo tile hosted on the cell to the ammo tile passed in as argument.
     * @param ammoTile the new ammo tile.
     */
    public void setAmmoTile(AmmoTile ammoTile) {
        this.ammoTile = ammoTile;
    }
}