package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.exceptions.InvalidEffectTypeException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public abstract class Effect {
    protected EffectType type;
    protected AttackPattern context;
    protected Player author;

    public static Effect build(DecoratedJsonObject jEffect) throws InvalidEffectTypeException {
        String type;
        try {
            type = jEffect.getString("type");
        } catch (JullPointerException e) {
            throw new JsonException("Effect type not found.");
        }

        if (type.equals("damage")) {
            List<Constraint> constraints = new ArrayList<>();
            int amount;
            try {
                amount = jEffect.getInt("amount");
            } catch (JullPointerException e) {
                throw new JsonException("Damage amount not found.");
            }
            List<DecoratedJsonObject> jConstraints;
            try {
                jConstraints = jEffect.getArray("constraints").asList();
            } catch (JullPointerException e) {
                throw new JsonException("Damage constraints not found.");
            }
            for (DecoratedJsonObject jConstraint : jConstraints) {
                constraints.add(Constraint.build(jConstraint));
            }
            return new Damage(amount, constraints);
        }
        if (type.equals("mark")) {
            List<Constraint> constraints = new ArrayList<>();
            int amount;
            try {
                amount = jEffect.getInt("amount");
            } catch (JullPointerException e) {
                throw new JsonException("Mark amount not found.");
            }
            List<DecoratedJsonObject> jConstraints;
            try {
                jConstraints = jEffect.getArray("constraints").asList();
            } catch (JullPointerException e) {
                throw new JsonException("Mark constraints not found.");
            }
            for (DecoratedJsonObject jConstraint : jConstraints) {
                constraints.add(Constraint.build(jConstraint));
            }
            return new Mark(amount, constraints);
        }
        if (type.equals("move")) {
            int sourceAttackModuleId;
            try {
                sourceAttackModuleId = jEffect.getInt("sourceAttackModuleId");
            } catch (JullPointerException e) {
                throw new JsonException("Move sourceAttackModuleId not found.");
            }
            int sourceTargetId;
            try {
                sourceTargetId = jEffect.getInt("sourceTargetId");
            } catch (JullPointerException e) {
                throw new JsonException("Move sourceTargetId not found.");
            }
            int drainAttackModuleId;
            try {
                drainAttackModuleId = jEffect.getInt("drainAttackModuleId");
            } catch (JullPointerException e) {
                throw new JsonException("Move drainAttackModuleId not found.");
            }
            int drainTargetId;
            try {
                drainTargetId = jEffect.getInt("drainTargetId");
            } catch (JullPointerException e) {
                throw new JsonException("Move drainTargetId not found.");
            }
            return new Move(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId);
        }
        throw new InvalidEffectTypeException(type + " is not a valid name for an Effect type. Use \"damage\", \"mark\", or \"move\"");
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
