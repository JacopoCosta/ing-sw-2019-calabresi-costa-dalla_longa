package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.exceptions.InvalidEffectTypeException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Effect}s are consequences of attacks that change a player's status.
 */
public abstract class Effect {
    /**
     * The {@code Effect}'s type.
     */
    protected EffectType type;

    /**
     * The context the {@code Effect} is being caused in.
     */
    protected AttackPattern context;

    /**
     * The player who caused the {@code Effect} to happen.
     */
    protected Player author;

    /**
     * This factory method constructs an {@code Effect}, with the properties found inside the JSON object passed as argument.
     * @param jEffect the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     * @throws InvalidEffectTypeException when attempting to instantiate a new {@code Effect} whose type is not in the
     * enumeration of possible effect types.
     */
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
                jConstraints = jEffect.getArray("constraints").toList();
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
                jConstraints = jEffect.getArray("constraints").toList();
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

    /**
     * Sets the {@code Effect}'s author.
     * @param author the {@code Effect}'s author.
     */
    public void setAuthor(Player author) {
        this.author = author;
    }

    /**
     * Returns the {@code Effect}'s author.
     * @return the {@code Effect}'s author.
     */
    public Player getAuthor() {
        return author;
    }

    /**
     * Sets the {@code Effect}'s context.
     * @param context the context the {@code Effect} is being caused in.
     */
    public void setContext(AttackPattern context) {
        this.context = context;
    }

    /**
     * Returns the {@code Effect}'s type.
     * @return the {@code Effect}'s type.
     */
    public EffectType getType() {
        return type;
    }

    /**
     * Causes the {@code Effect}'s consequences to happen.
     */
    public abstract void apply();

    @Override
    public abstract String toString();
}
