package it.polimi.ingsw.weaponry;

import it.polimi.ingsw.ammo.AmmoCubes;

import java.util.List;

public class Weapon {
    private String name;
    private List<Action> actions;

    private AmmoCubes purchaseCost;
    private AmmoCubes reloadCost;
    private boolean loaded;

    private Weapon(String name, List<Action> actions, AmmoCubes purchaseCost, AmmoCubes reloadCost) {
        this.name = name;
        this.actions = actions;
        this.purchaseCost = purchaseCost;
        this.reloadCost = reloadCost;
        this.loaded = false;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void reload() {
        this.loaded = true;
    }

    public static Weapon build(String s) {
        return null; //TODO weapon factory
    }
}
