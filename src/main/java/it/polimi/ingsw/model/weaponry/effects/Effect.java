package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.exceptions.InvalidEffectTypeException;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import it.polimi.ingsw.model.weaponry.Attack;

// an effect modifies the state of a player
// a group of effects makes an attack
public abstract class Effect {
    protected EffectType type;
    protected int amount;


    public static Effect build(DecoratedJSONObject jEffect) throws InvalidEffectTypeException {
        String type = jEffect.getString("type");
        int amount = jEffect.getInt("amount");

        if(type.equals("damage"))
            return new Damage(amount);
        if(type.equals("forcemove"))
            return new ForceMove(amount);
        if(type.equals("mark"))
            return new Mark(amount);
        if(type.equals("selfmove"))
            return new SelfMove(amount);
        throw new InvalidEffectTypeException(type + " is not a valid name for an Effect type. Use \"damage\", \"forcemove\", \"mark\", or \"selfmove\"");
    }

    public abstract void apply(Attack attack);
}
