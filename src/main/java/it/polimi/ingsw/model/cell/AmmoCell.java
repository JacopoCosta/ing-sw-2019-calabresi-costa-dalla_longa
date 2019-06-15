package it.polimi.ingsw.model.cell;

import it.polimi.ingsw.model.ammo.AmmoTile;

/**
 * {@link AmmoCell}s are {@link Cell}s that are able to host an {@link AmmoTile} on them.
 */
public class AmmoCell extends Cell {

    /**
     * The {@link AmmoTile} hosted on the {@link Cell}. It is null when the {@link Cell} is empty.
     */
    private AmmoTile ammoTile;

    /**
     * This is the only constructor.
     * @param xCoord the x (horizontal) coordinate in the 2-dimensional discrete space this {@link Cell} will be put at.
     * @param yCoord the y (vertical) coordinate in the 2-dimensional discrete space this {@link Cell} will be put at.
     */
    public AmmoCell(int xCoord, int yCoord) {
        super(xCoord, yCoord);
        this.spawnPoint = false;
    }

    /**
     * Returns the {@link AmmoTile} hosted on the {@link Cell}.
     * @return the {@link AmmoTile}.
     */
    public AmmoTile getAmmoTile() {
        return ammoTile;
    }

    /**
     * Sets the {@link AmmoTile} hosted on the {@link Cell} to the {@link AmmoTile} passed in as argument.
     * @param ammoTile the new {@link AmmoTile}.
     */
    public void setAmmoTile(AmmoTile ammoTile) {
        this.ammoTile = ammoTile;
    }
}