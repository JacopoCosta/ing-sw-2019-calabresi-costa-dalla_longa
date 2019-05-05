package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

// an ammo tile depicts some ammo cubes and may include the ability to draw 1 power-up card
public class AmmoTile {
    private AmmoCubes ammoCubes;
    private boolean includesPowerUp;

    private AmmoTile(AmmoCubes ammoCubes, boolean includesPowerUp) {
        this.ammoCubes = ammoCubes;
        this.includesPowerUp = includesPowerUp;
    }

    public static AmmoTile build(DecoratedJSONObject jAmmoTile) {
        DecoratedJSONObject jAmmoCube = jAmmoTile.getObject("ammoCubes");
        AmmoCubes ammoCubes = AmmoCubes.build(jAmmoCube);
        boolean includesPowerUp = jAmmoTile.getBoolean("includesPowerUp");
        return new AmmoTile(ammoCubes, includesPowerUp);
    }

    public boolean includesPowerUp() {
        return includesPowerUp;
    }

    public String toString() {
        return ammoCubes.toString() + (includesPowerUp ? " with powerup\n" : "\n");
    }
}
