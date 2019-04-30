package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.weaponry.Attack;

import java.util.List;

// an effect modifies the state of a player
// a group of effects makes an attack
public abstract class Effect {
    protected EffectType type;
    protected int amount;

    public static Effect build(List<String> descriptors) {
        String type = descriptors.remove(0);
        int amount = Integer.parseInt(descriptors.remove(0));

        if(type.equals("D"))
            return new Damage(amount);
        if(type.equals("M"))
            return new Mark(amount);
        if(type.equals("F"))
            return new ForceMove(amount);
        return null;
    }

    public abstract void apply(Attack attack);
}
