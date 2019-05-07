package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.player.ActiveAction;

import java.util.List;

public class VisibilityConstraint extends Constraint {
    private boolean sees;

    public VisibilityConstraint(int sourceActionId, int sourceAttackId, int drainActionId, int drainAttackId, boolean sees) {
        this.sourceActionId = sourceActionId;
        this.sourceAttackId = sourceAttackId;
        this.drainActionId = drainActionId;
        this.drainAttackId = drainAttackId;
        this.sees = sees;
        this.type = ConstraintType.VISIBILITY;
    }

    @Override
    public void verify(List<ActiveAction> activeActions) throws ConstraintNotSatisfiedException {
        Cell sourceCell = getPlayerFromId(activeActions, sourceActionId, sourceAttackId).getPosition();
        Cell drainCell = getPlayerFromId(activeActions, drainActionId, drainAttackId).getPosition();

        if(sourceCell.canSee(drainCell) != sees)
            throw new ConstraintNotSatisfiedException("The two cells don't satisfy the visibility constraint.");
    }
}
