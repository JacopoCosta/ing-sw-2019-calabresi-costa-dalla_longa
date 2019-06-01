package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;

public class Weapon {
    private String name;
    private AmmoCubes purchaseCost;
    private AmmoCubes reloadCost;
    private AttackPattern attackPattern;

    private boolean loaded;

    private Weapon(String name, AmmoCubes purchaseCost, AmmoCubes reloadCost, AttackPattern attackPattern) {
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.reloadCost = reloadCost;
        this.attackPattern = attackPattern;
        this.loaded = true;
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

    public AttackPattern getPattern() {
        return attackPattern;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void reload() throws WeaponAlreadyLoadedException {
        if(this.loaded)
            throw new WeaponAlreadyLoadedException("Attempted to reload an already loaded weapon");
        this.loaded = true;
    }

    public void unload() throws WeaponAlreadyUnloadedException {
        if(!this.loaded)
            throw new WeaponAlreadyUnloadedException("Attempted to consume ammo from an unloaded weapon.");
        this.loaded = false;
    }

    public static Weapon build(DecoratedJsonObject jWeapon) {
        String name;
        try {
            name = jWeapon.getString("name");
        } catch (JullPointerException e) {
            throw new JsonException("Weapon name not found");
        }
        AmmoCubes purchaseCost;
        try {
            purchaseCost = AmmoCubes.build(jWeapon.getObject("purchaseCost"));
        } catch (JullPointerException e) {
            throw new JsonException("Weapon purchaseCost not found.");
        }
        AmmoCubes reloadCost;
        try {
            reloadCost = AmmoCubes.build(jWeapon.getObject("reloadCost"));
        } catch (JullPointerException e) {
            throw new JsonException("Weapon reloadCost not found.");
        }
        AttackPattern attackPattern;
        try {
            attackPattern = AttackPattern.build(jWeapon.getObject("attackPattern"));
        } catch (JullPointerException e) {
            throw new JsonException("Weapon attackPattern not found,");
        }

        return new Weapon(name, purchaseCost, reloadCost, attackPattern);
    }

    @Override
    public String toString() {
        try {
            AmmoCubes difference = reloadCost.take(purchaseCost);
            return name + " [" + purchaseCost + "(" + difference + ")]";
        } catch (CannotAffordException ignored) {
            return toString();
        }
    }
}
