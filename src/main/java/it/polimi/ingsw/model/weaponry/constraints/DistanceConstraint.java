package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.player.ActiveAction;

import java.util.List;

public class DistanceConstraint extends Constraint {
    private int lowerBound;
    private int upperBound;

    public DistanceConstraint(int sourceActionId, int sourceAttackId, int drainActionId, int drainAttackId, int lowerBound, int upperBound) {
        this.sourceActionId = sourceActionId;
        this.sourceAttackId = sourceAttackId;
        this.drainActionId = drainActionId;
        this.drainAttackId = drainAttackId;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.type = ConstraintType.DISTANCE;
    }

    @Override
    public void verify(List<ActiveAction> activeActions) throws ConstraintNotSatisfiedException {
        Cell c1 = getPlayerFromId(activeActions, sourceActionId, sourceAttackId).getPosition();
        Cell c2 = getPlayerFromId(activeActions, drainActionId, drainAttackId).getPosition();

        int distance = c1.distance(c2);

        if(distance < lowerBound)
            throw new ConstraintNotSatisfiedException("The distance is too tiny.");
        if(distance > upperBound && upperBound >= 0)
            throw new ConstraintNotSatisfiedException("The distance is too great.");
    }
}
