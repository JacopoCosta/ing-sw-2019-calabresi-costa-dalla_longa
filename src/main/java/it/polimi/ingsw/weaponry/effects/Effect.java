package it.polimi.ingsw.weaponry.effects;

import it.polimi.ingsw.weaponry.Attack;

// an effect modifies the state of a player
// a group of effects makes an attack
public abstract class Effect {
    protected int amount;

    protected boolean cellDamage; // whether or not this Effect is applied to all Players in the same cell
    protected boolean roomDamage; // whether or not this Effect is applied to all Players in the same room

    public abstract void apply(Attack attack);
}
