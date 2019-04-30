package it.polimi.ingsw.model.cell;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.weaponry.Weapon;

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

    public List<Weapon> getWeaponShop() {
        return this.weaponShop;
    }

    public void addToWeaponShop(Weapon weapon) {
        this.weaponShop.add(weapon);
    }

    public Weapon takeFromWeaponShop(int index) {
        return this.weaponShop.remove(index);
    }
}