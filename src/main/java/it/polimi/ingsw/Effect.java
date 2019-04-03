package it.polimi.ingsw;

// an effect modifies the state of a player
// a group of effects makes an attack
public abstract class Effect {
    protected int amount;

    public abstract void apply(Attack attack);
}
