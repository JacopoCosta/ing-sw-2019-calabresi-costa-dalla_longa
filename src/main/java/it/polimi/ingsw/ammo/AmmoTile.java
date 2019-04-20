package it.polimi.ingsw.ammo;

// an ammo tile depicts some ammo cubes and may include the ability to draw 1 power-up card
public class AmmoTile {
    private AmmoCubes ammoCubes;
    private boolean includesPowerup;

    private AmmoTile(AmmoCubes ammoCubes, boolean includesPowerup) {
        this.ammoCubes = ammoCubes;
        this.includesPowerup = includesPowerup;
    }

    public static AmmoTile build(String string) {
        return null; //TODO AmmoTile factory
    }
}
