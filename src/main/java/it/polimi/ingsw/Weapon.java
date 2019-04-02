package it.polimi.ingsw;

import java.util.List;

public class Weapon {
    private String name;
    private List<Action> actions;

    private AmmoCubes purchaseCost;
    private AmmoCubes reloadCost;
    private boolean loaded;

    public Weapon(String name, List<Action> actions, AmmoCubes purchaseCost, AmmoCubes reloadCost) {
        this.name = name;
        this.actions = actions;
        this.purchaseCost = purchaseCost;
        this.reloadCost = reloadCost;
        this.loaded = false;
    }
}
