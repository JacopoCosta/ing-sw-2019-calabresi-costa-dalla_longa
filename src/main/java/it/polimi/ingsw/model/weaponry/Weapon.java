package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.effects.Damage;
import it.polimi.ingsw.model.weaponry.effects.Mark;
import it.polimi.ingsw.model.weaponry.effects.Move;

/**
 * {@code Weapon}s are a special type of card used by the {@link Player} to {@link Move}, {@link Damage} and {@link Mark} other {@link Player}s.
 */

public class Weapon {
    /**
     * The name of the {@code Weapon}.
     */
    private String name;

    /**
     * The cost of purchasing the {@code Weapon} from a {@link SpawnCell}'s shop.
     */
    private AmmoCubes purchaseCost;

    /**
     * The cost of reloading the {@code Weapon} after having used it.
     */
    private AmmoCubes reloadCost;

    /**
     * A data structure that fully encompasses the {@code Weapon}'s behaviour and functionalities.
     */
    private AttackPattern attackPattern;

    /**
     * Whether or not the {@code Weapon} is loaded.
     */
    private boolean loaded;

    /**
     * This is the only constructor.
     * @param name the {@code Weapon}'s {@link Weapon#name}.
     * @param purchaseCost its {@link Weapon#purchaseCost}.
     * @param reloadCost its {@link Weapon#reloadCost}.
     * @param attackPattern its {@link Weapon#attackPattern}.
     */
    private Weapon(String name, AmmoCubes purchaseCost, AmmoCubes reloadCost, AttackPattern attackPattern) {
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.reloadCost = reloadCost;
        this.attackPattern = attackPattern;
        this.loaded = true;
    }

    /**
     * Gets the {@code Weapon}'s {@link Weapon#name}.
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the {@code Weapon}'s {@link Weapon#purchaseCost}.
     * @return the purchase cost.
     */
    public AmmoCubes getPurchaseCost() {
        return purchaseCost;
    }

    /**
     * Gets the {@code Weapon}'s {@link Weapon#reloadCost}.
     * @return the reload cost.
     */
    public AmmoCubes getReloadCost() {
        return reloadCost;
    }

    /**
     * Gets the {@code Weapon}'s {@link Weapon#attackPattern}.
     * @return the attack pattern.
     */
    public AttackPattern getPattern() {
        return attackPattern;
    }

    /**
     * Tells whether or not the {@code Weapon} is {@link Weapon#loaded}.
     * @return {@code true} if it is.
     */
    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * Reloads the {@code Weapon}.
     * @throws WeaponAlreadyLoadedException upon attempting to reload a loaded {@code Weapon}.
     */
    public void reload() throws WeaponAlreadyLoadedException {
        if (this.loaded)
            throw new WeaponAlreadyLoadedException("Attempted to reload an already loaded weapon");
        this.loaded = true;
    }

    /**
     * Unloads the {@code Weapon}.
     * @throws WeaponAlreadyUnloadedException upon attempting to unloaded an unloaded {@code Weapon}.
     */
    public void unload() throws WeaponAlreadyUnloadedException {
        if (!this.loaded)
            throw new WeaponAlreadyUnloadedException("Attempted to consume ammo from an unloaded weapon.");
        this.loaded = false;
    }

    /**
     * This factory method instantiates and returns a {@code Weapon}, with the properties found inside the JSON object passed as argument.
     * @param jWeapon the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     */
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

    /**
     * Generates a string containing a short description of the {@code Weapon} and its {@link AttackModule}s.
     * @return
     */
    @Override
    public String toString() {
        try {
            AmmoCubes difference = reloadCost.take(purchaseCost);
            String string = name + " [" + purchaseCost + "(" + difference + ")]";
            if (!loaded)
                string += "*";
            return string;
        } catch (CannotAffordException ignored) {
            return toString();
        }
    }

    /**
     * This is an alternate, more user-friendly version of {@link Weapon#toString()}.
     * @return the {@code Weapon}'s description.
     */
    public String getDescription() {
        return name + ":" + attackPattern.toString();
    }
}
