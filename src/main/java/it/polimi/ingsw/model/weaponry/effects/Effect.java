package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.exceptions.InvalidEffectTypeException;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public abstract class Effect {
    protected EffectType type;
    protected Object context;

    public static Effect build(DecoratedJSONObject jEffect) throws InvalidEffectTypeException {
        String type = jEffect.getString("type");

        if(type.equals("damage")) {
            List<Constraint> constraints = new ArrayList<>();
            int amount = jEffect.getInt("amount");
            List<DecoratedJSONObject> jConstraints = jEffect.getArray("constraints").asList();
            for(DecoratedJSONObject jConstraint : jConstraints) {
                constraints.add(Constraint.build(jConstraint));
            }
            return new Damage(amount, constraints);
        }
        if(type.equals("mark")) {
            List<Constraint> constraints = new ArrayList<>();
            int amount = jEffect.getInt("amount");
            List<DecoratedJSONObject> jConstraints = jEffect.getArray("constraints").asList();
            for(DecoratedJSONObject jConstraint : jConstraints) {
                constraints.add(Constraint.build(jConstraint));
            }
            return new Mark(amount, constraints);
        }
        if(type.equals("move")) {
            int sourceAttackModuleId = jEffect.getInt("sourceAttackModuleId");
            int sourceTargetId = jEffect.getInt("sourceTargetId");
            int drainAttackModuleId = jEffect.getInt("drainAttackModuleId");
            int drainTargetId = jEffect.getInt("drainTargetId");
            return new Move(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId);
        }
        throw new InvalidEffectTypeException(type + " is not a valid name for an Effect type. Use \"damage\", \"mark\", or \"move\"");
    }

    public abstract void apply();

    @Override
    public abstract String toString();
}
