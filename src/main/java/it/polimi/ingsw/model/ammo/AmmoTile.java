package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;

/**
 * Ammo tiles are tokens spread on the cells that are not spawn points.
 * Each ammo tile allows for the collection of 2~3 ammo cubes, and may include the ability to draw once from the power up deck.
 * @see it.polimi.ingsw.model.cell.AmmoCell
 */
public class AmmoTile {
    /**
     * The amount of ammo cubes the player gains when collecting this ammo tile.
     */
    private AmmoCubes ammoCubes;

    /**
     * Whether or not a power up is included in the ammo tile.
     */
    private boolean includesPowerUp;

    /**
     * This is the only constructor.
     * @param ammoCubes the ammo cube set included.
     * @param includesPowerUp whether or not to include a power up.
     */
    public AmmoTile(AmmoCubes ammoCubes, boolean includesPowerUp) {
        this.ammoCubes = ammoCubes;
        this.includesPowerUp = includesPowerUp;
    }

    /**
     * This factory method constructs an object with the properties found inside the JSON object passed as argument.
     * @param jAmmoTile the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     */
    public static AmmoTile build(DecoratedJsonObject jAmmoTile) {
        DecoratedJsonObject jAmmoCubes;
        try {
            jAmmoCubes = jAmmoTile.getObject("ammoCubes");
        } catch (JullPointerException e) {
            throw new JsonException("AmmoTile ammoCubes not found.");
        }
        AmmoCubes ammoCubes = AmmoCubes.build(jAmmoCubes);
        boolean includesPowerUp;
        try {
            includesPowerUp = jAmmoTile.getBoolean("includesPowerUp");
        } catch (JullPointerException e) {
            throw new JsonException("AmmoTile includesPowerUp not found.");
        }
        return new AmmoTile(ammoCubes, includesPowerUp);
    }

    /**
     * Returns the amount of ammo cubes included in the ammo tile.
     * @return the amount of ammo cubes included in the ammo tile.
     */
    public AmmoCubes getAmmoCubes() {
        return ammoCubes;
    }

    /**
     * Returns whether or not a power up is included in the ammo tile.
     * @return whether or not a power up is included in the ammo tile.
     */
    public boolean includesPowerUp() {
        return includesPowerUp;
    }

    @Override
    public String toString() {
        return ammoCubes.toString() + (includesPowerUp ? "+P" : "");
    }
}
