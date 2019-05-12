package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidTargetTypeException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.List;

public abstract class Target {
    protected TargetType type;
    protected String message;
    protected List<Constraint> constraints;
    protected Object context;

    public static Target build(DecoratedJSONObject jTarget) {
        String type = jTarget.getString("type");

        List<Constraint> constraints = new ArrayList<>();
        String message = jTarget.getString("message");
        List<DecoratedJSONObject> jConstraints = jTarget.getArray("constraints").asList();
        for(DecoratedJSONObject jConstraint : jConstraints) {
            constraints.add(Constraint.build(jConstraint));
        }

        if(type.equals("player")) {
            return new TargetPlayer(message, constraints);
        }
        else if(type.equals("cell")) {
            return new TargetCell(message, constraints);
        }
        else if(type.equals("room")) {
            return new TargetRoom(message, constraints);
        }
        else
            throw new InvalidTargetTypeException(type  + " is not a valid name for a Target type. Use \"player\", \"cell\", or \"room\"");
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

    public abstract Player getPlayer();

    public abstract Cell getCell();

    public abstract Room getRoom();

    @Override
    public String toString() {
        String s = type.toString().toLowerCase();
        for(Constraint constraint : constraints)
            s += " (" + constraint.toString() + ")";
        return s;
    }
}
