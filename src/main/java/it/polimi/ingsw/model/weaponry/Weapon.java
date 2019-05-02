package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.WeaponAlreadyLoadedException;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

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

    public String getName() {
        return this.name;
    }

    public AmmoCubes getPurchaseCost() {
        return purchaseCost;
    }

    public AmmoCubes getReloadCost() {
        return reloadCost;
    }

    public List<Action> getActions() {
        return actions;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void reload() throws WeaponAlreadyLoadedException{
        if(this.loaded)
            throw new WeaponAlreadyLoadedException("Attempted to reload an already loaded weapon");
        this.loaded = true;
    }

    public static Weapon build(DecoratedJSONObject jWeapon) {
        String name = jWeapon.getString("name");
        int red, yellow, blue;
        AmmoCubes purchaseCost = AmmoCubes.build(jWeapon.getObject("purchaseCost"));
        AmmoCubes reloadCost = AmmoCubes.build(jWeapon.getObject("reloadCost"));
        List<Action> actions = new ArrayList<>();

        for(DecoratedJSONObject jAction : jWeapon.getArray("actions").asList()) {
            actions.add(Action.build(jAction));
        }
        return new Weapon(name, purchaseCost, reloadCost, actions);
    }
}
