package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.exceptions.InvalidEffectTypeException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public abstract class Effect {
    protected EffectType type;
    protected AttackPattern context;
    protected Player author;

    public static Effect build(DecoratedJsonObject jEffect) throws InvalidEffectTypeException {
        try {
            String type = jEffect.getString("type");

            if (type.equals("damage")) {
                List<Constraint> constraints = new ArrayList<>();
                int amount = jEffect.getInt("amount");
                List<DecoratedJsonObject> jConstraints = jEffect.getArray("constraints").asList();
                for (DecoratedJsonObject jConstraint : jConstraints) {
                    constraints.add(Constraint.build(jConstraint));
                }
                return new Damage(amount, constraints);
            }
            if (type.equals("mark")) {
                List<Constraint> constraints = new ArrayList<>();
                int amount = jEffect.getInt("amount");
                List<DecoratedJsonObject> jConstraints = jEffect.getArray("constraints").asList();
                for (DecoratedJsonObject jConstraint : jConstraints) {
                    constraints.add(Constraint.build(jConstraint));
                }
                return new Mark(amount, constraints);
            }
            if (type.equals("move")) {
                int sourceAttackModuleId = jEffect.getInt("sourceAttackModuleId");
                int sourceTargetId = jEffect.getInt("sourceTargetId");
                int drainAttackModuleId = jEffect.getInt("drainAttackModuleId");
                int drainTargetId = jEffect.getInt("drainTargetId");
                return new Move(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId);
            }
            throw new InvalidEffectTypeException(type + " is not a valid name for an Effect type. Use \"damage\", \"mark\", or \"move\"");
        } catch (JullPointerException e) {
            throw new JsonException("Can't load effect");
        }
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public Player getAuthor() {
        return author;
    }

    public void setContext(AttackPattern context) {
        this.context = context;
    }

    public EffectType getType() {
        return type;
    }

    public abstract void apply();

    @Override
    public abstract String toString();
}
