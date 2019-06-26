package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;

/**
 * {@link AmmoTile}s are tokens spread on the {@link Cell}s that are not {@link SpawnCell}s.
 * Each {@link AmmoTile} allows for the collection of 2~3 {@link AmmoCubes}, and may include the ability to draw once
 * from the {@link PowerUp} {@link Deck}.
 * @see it.polimi.ingsw.model.cell.AmmoCell
 */
public class AmmoTile {
    /**
     * The amount of {@link AmmoCubes} the player gains when collecting this {@link AmmoTile}.
     */
    private AmmoCubes ammoCubes;

    /**
     * Whether or not a {@link PowerUp} is included in the {@link AmmoTile}.
     */
    private boolean includesPowerUp;

    /**
     * This is the only constructor.
     * @param ammoCubes the {@link AmmoCubes} set included.
     * @param includesPowerUp whether or not to include a {@link PowerUp}.
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
     * Returns the amount of {@link AmmoCubes} included in the {@link AmmoTile}.
     * @return that amount.
     */
    public AmmoCubes getAmmoCubes() {
        return ammoCubes;
    }

    /**
     * Returns whether or not a {@link PowerUp} is included in the {@link AmmoTile}.
     * @return true if it is included.
     */
    public boolean includesPowerUp() {
        return includesPowerUp;
    }

    /**
     * Creates a short description of the ammo tile.
     * @return the description.
     */
    @Override
    public String toString() {
        return ammoCubes.toString() + (includesPowerUp ? "+P" : "");
    }
}
