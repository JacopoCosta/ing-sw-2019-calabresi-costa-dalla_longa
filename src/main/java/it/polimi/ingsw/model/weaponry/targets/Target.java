package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidTargetTypeException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public abstract class Target {
    protected TargetType type;
    protected String message;
    protected List<Constraint> constraints;
    protected AttackPattern context;

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
            jConstraints = jTarget.getArray("constraints").asList();
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

    public TargetType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(AttackPattern context) {
        this.context = context;
        for(Constraint constraint : constraints)
            constraint.setContext(context);
    }

    public abstract Player getPlayer();

    public abstract Cell getCell();

    public abstract Room getRoom();

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(type.toString().toLowerCase());
        for(Constraint constraint : constraints)
            s.append(" (").append(constraint.toString()).append(")");
        return s.toString();
    }
}
