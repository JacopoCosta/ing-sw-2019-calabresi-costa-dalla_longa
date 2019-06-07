package it.polimi.ingsw.model.cell;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawn cells are cells that players can choose as spawn points.
 * Each spawn cell also includes a display of weapons available to be purchased by players,
 * so long as they have enough ammo to be able to afford the purchase cost of the weapon.
 */
public class SpawnCell extends Cell {
    /**
     * This attribute is used to determine which cell a given power up refers to as its spawn point.
     * @see PowerUp#getAmmoCubes()
     */
    private AmmoCubes ammoCubeColor; // this attribute is used to determine which Cell a given PowerUp refers to as its spawnPoint

    /**
     * The list of weapons available to buy.
     */
    private List<Weapon> weaponShop;

    /**
     * This is the only constructor.
     * @param xCoord the x (horizontal) coordinate in the 2-dimensional discrete space this cell will be put at.
     * @param yCoord the y (vertical) coordinate in the 2-dimensional discrete space this cell will be put at.
     * @param ammoCubeColor the colour of the spawn point.
     */
    public SpawnCell(int xCoord, int yCoord, AmmoCubes ammoCubeColor) {
        super(xCoord, yCoord);
        this.ammoCubeColor = ammoCubeColor;
        this.weaponShop = new ArrayList<>();
        this.spawnPoint = true;
    }

    /**
     * Returns a set of ammo cubes containing only one cube of the colour used to identify the spawn point.
     * @return a set of ammo cubes containing only one cube of the colour used to identify the spawn point.
     */
    public AmmoCubes getAmmoCubeColor() {
        return this.ammoCubeColor;
    }

    /**
     * Returns a list containing the weapons the cell offers.
     * @return a list containing the weapons the cell offers.
     */
    public List<Weapon> getWeaponShop() {
        return this.weaponShop;
    }

    /**
     * Adds a weapon to the weapon shop.
     * @param weapon the weapon to add.
     */
    public void addToWeaponShop(Weapon weapon) {
        this.weaponShop.add(weapon);
    }

    /**
     * Removes a weapon from the weapon shop.
     * @param index the index of the weapon to remove in the list of weapons.
     * @return the removed weapon.
     */
    public Weapon takeFromWeaponShop(int index) {
        return this.weaponShop.remove(index);
    }
}