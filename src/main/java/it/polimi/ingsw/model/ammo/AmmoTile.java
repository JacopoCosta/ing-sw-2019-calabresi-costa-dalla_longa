package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;

// an ammo tile depicts some ammo cubes and may include the ability to draw 1 power-up card
public class AmmoTile {
    private AmmoCubes ammoCubes;
    private boolean includesPowerUp;

    public AmmoTile(AmmoCubes ammoCubes, boolean includesPowerUp) {
        this.ammoCubes = ammoCubes;
        this.includesPowerUp = includesPowerUp;
    }

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

    public AmmoCubes getAmmoCubes() {
        return ammoCubes;
    }

    public boolean includesPowerUp() {
        return includesPowerUp;
    }

    @Override
    public String toString() {
        return ammoCubes.toString() + (includesPowerUp ? "+P" : "");
    }
}
