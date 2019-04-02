package it.polimi.ingsw;

public abstract class Effect {
    protected int amount;

    public abstract void apply(Attack attack);
}
