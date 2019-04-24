package it.polimi.ingsw.weaponry;

import it.polimi.ingsw.ammo.AmmoCubes;

import java.util.ArrayList;
import java.util.List;

public class Weapon {
    private String name;
    private AmmoCubes purchaseCost;
    private AmmoCubes reloadCost;
    private List<Action> actions;

    private boolean loaded;

    private Weapon(String name, AmmoCubes purchaseCost, AmmoCubes reloadCost, List<Action> actions) {
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.reloadCost = reloadCost;
        this.actions = actions;
        this.loaded = false;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void reload() {
        this.loaded = true;
    }

    public static Weapon build(List<String> descriptors) {
        String name = descriptors.remove(0);
        AmmoCubes purchaseCost = AmmoCubes.build(descriptors.remove(0));
        AmmoCubes reloadCost = AmmoCubes.build(descriptors.remove(0));
        List<Action> actions = new ArrayList<>();
        List<String> actionDescriptors = new ArrayList<>();
        for(String s : descriptors) {
            if(s.equals("A")) {
                actions.add(Action.build(actionDescriptors));
                actionDescriptors.clear();
            }
            else
                actionDescriptors.add(s);
        }
        return new Weapon(name, purchaseCost, reloadCost, actions);
    }
}
