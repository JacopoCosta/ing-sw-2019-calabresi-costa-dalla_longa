package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.WeaponAlreadyLoadedException;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

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

    public AttackPattern getPattern() {
        return attackPattern;
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
        AmmoCubes purchaseCost = AmmoCubes.build(jWeapon.getObject("purchaseCost"));
        AmmoCubes reloadCost = AmmoCubes.build(jWeapon.getObject("reloadCost"));
        AttackPattern attackPattern = AttackPattern.build(jWeapon.getObject("attackPattern"));

        return new Weapon(name, purchaseCost, reloadCost, attackPattern);
    }

    @Override
    public String toString() {
        String s = name + ":\n";
        s += purchaseCost.toString() + " to purchase\n";
        s += reloadCost.toString() + " to reload\n";
        s += attackPattern.toString() + "\n";
        return s;
    }
}
