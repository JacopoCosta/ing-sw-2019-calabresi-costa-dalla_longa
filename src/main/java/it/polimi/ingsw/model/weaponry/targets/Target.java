package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidTargetTypeException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Target}s are the actors that have a role in attacks. Each {@link AttackModule}
 * has a list of {@code Target}s, each of which corresponds to a choice made by the attacker. {@code Target}s can be {@link Room}s,
 * {@link Cell}s or {@link Player}s.
 */
public abstract class Target {
    /**
     * The type of the {@code Target}.
     */
    protected TargetType type;

    /**
     * The message presented to the user when they need to make a decision about the choice entailed by the {@code Target}.
     */
    protected String message;

    /**
     * A list of rules that the user's choice should abide by.
     */
    protected List<Constraint> constraints;

    /**
     * The context in which the choice is made.
     */
    protected AttackPattern context;

    /**
     * This factory method constructs a {@code Target}, with the properties found inside the JSON object passed as argument.
     * @param jTarget the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     * @throws InvalidTargetTypeException when attempting to instantiate a new {@code Target} whose type is not in the
     * enumeration of possible {@code Target} types.
     */
    public static Target build(DecoratedJsonObject jTarget) {
        String type;
        try {
            type = jTarget.getString("type");
        } catch (JullPointerException e) {
            throw new JsonException("Target type not found.");
        }
        List<Constraint> constraints = new ArrayList<>();

        String message;
        try {
            message = jTarget.getString("message");
        } catch (JullPointerException e) {
            throw new JsonException("Target message not found.");
        }
        List<DecoratedJsonObject> jConstraints;
        try {
            jConstraints = jTarget.getArray("constraints").toList();
        } catch (JullPointerException e) {
            throw new JsonException("Target constraints not found.");
        }
        for(DecoratedJsonObject jConstraint : jConstraints) {
            constraints.add(Constraint.build(jConstraint));
        }

        switch (type) {
            case "player":
                return new TargetPlayer(message, constraints);
            case "cell":
                return new TargetCell(message, constraints);
            case "room":
                return new TargetRoom(message, constraints);
            default:
                throw new InvalidTargetTypeException(type + " is not a valid name for a Target type. Use \"player\", \"cell\", or \"room\"");
        }
    }

    /**
     * Tells what {@link Target#type} the {@code Target} is.
     * @return the type.
     */
    public TargetType getType() {
        return type;
    }

    /**
     * Tells what the {@code Target}'s {@link Target#message} is.
     * @return the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Tells what the {@code Target}'s {@link Target#constraints} are.
     * @return a list containing them.
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Tells what the {@code Target}'s {@link Target#context} is.
     * @return the relevant {@link AttackPattern}.
     */
    public Object getContext() {
        return context;
    }

    /**
     * Sets what the {@code Target}'s {@link Target#context} should be.
     * @param context the relevant {@link AttackPattern}.
     */
    public void setContext(AttackPattern context) {
        this.context = context;
        for(Constraint constraint : constraints)
            constraint.setContext(context);
    }

    public abstract Player getPlayer();

    public abstract Cell getCell();

    public abstract Room getRoom();

    /**
     * Generates a string containing a short description of the {@code Target}.
     * @return the string.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(type.toString().toLowerCase());
        for(Constraint constraint : constraints)
            s.append(" (").append(constraint.toString()).append(")");
        return s.toString();
    }
}
