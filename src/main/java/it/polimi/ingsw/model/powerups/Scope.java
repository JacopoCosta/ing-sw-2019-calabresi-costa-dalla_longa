package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;

/**
 * Scopes allow an attacker to deal one additional damage to one of their targets for each scope used.
 * Using a scope requires also paying a cost equal to one ammo cube of any colour.
 */
public class Scope extends PowerUp{
    /**
     * This is the only constructor.
     * @param ammoCubes a set of ammo cubes containing only one cube of the colour of the power up.
     */
    public Scope(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.SCOPE;
    }
}
