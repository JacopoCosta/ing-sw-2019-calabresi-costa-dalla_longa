package it.polimi.ingsw.model.cell;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link SpawnCell}s are {@link Cell}s that {@link Player}s can choose as spawn points.
 * Each {@link SpawnCell} also includes a display of {@link Weapon}s available to be purchased by {@link Player}s,
 * so long as they have enough ammo to be able to afford the purchase cost of the {@link Weapon}.
 */
public class SpawnCell extends Cell {
    /**
     * This attribute is used to determine which {@link SpawnCell} a given {@link PowerUp} refers to as its spawn point.
     * @see PowerUp#getAmmoCubes()
     */
    private AmmoCubes ammoCubeColor; // this attribute is used to determine which Cell a given PowerUp refers to as its spawnPoint

    /**
     * The list of {@link Weapon}s available to buy.
     */
    private List<Weapon> weaponShop;

    /**
     * This is the only constructor.
     * @param xCoord the x (horizontal) coordinate in the 2-dimensional discrete space this {@link Cell} will be put at.
     * @param yCoord the y (vertical) coordinate in the 2-dimensional discrete space this {@link Cell} will be put at.
     * @param ammoCubeColor the colour of the {@link SpawnCell}.
     */
    public SpawnCell(int xCoord, int yCoord, AmmoCubes ammoCubeColor) {
        super(xCoord, yCoord);
        this.ammoCubeColor = ammoCubeColor;
        this.weaponShop = new ArrayList<>();
        this.spawnPoint = true;
    }

    /**
     * Returns a set of {@link AmmoCubes} containing only one cube of the colour used to identify the {@link SpawnCell}.
     * @return the set.
     */
    public AmmoCubes getAmmoCubeColor() {
        return this.ammoCubeColor;
    }

    /**
     * Returns a list containing the {@link Weapon}s the {@link Cell} offers.
     * @return a list containing the {@link Weapon}s the {@link Cell} offers.
     */
    public List<Weapon> getWeaponShop() {
        return this.weaponShop;
    }

    /**
     * Adds a {@link Weapon} to the {@link Weapon} shop.
     * @param {@link Weapon} the {@link Weapon} to add.
     */
    public void addToWeaponShop(Weapon weapon) {
        this.weaponShop.add(weapon);
    }

    /**
     * Removes a {@link Weapon} from the {@link Weapon} shop.
     * @param index the index of the {@link Weapon} to remove in the list of {@link Weapon}s.
     * @return the removed {@link Weapon}.
     */
    public Weapon takeFromWeaponShop(int index) {
        return this.weaponShop.remove(index);
    }
}