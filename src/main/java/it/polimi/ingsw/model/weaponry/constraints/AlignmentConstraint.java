package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.player.ActiveAction;

import java.util.List;

public class AlignmentConstraint extends Constraint {
    public AlignmentConstraint(int sourceActionId, int sourceAttackId, int drainActionId, int drainAttackId) {
        this.sourceActionId = sourceActionId;
        this.sourceAttackId = sourceAttackId;
        this.drainActionId = drainActionId;
        this.drainAttackId = drainAttackId;
        this.type = ConstraintType.ALIGNMENT;
    }

    @Override
    public void verify(List<ActiveAction> activeActions) throws ConstraintNotSatisfiedException {
        Cell sourceCell = getPlayerFromId(activeActions, sourceActionId, sourceAttackId).getPosition();
        Cell drainCell = getPlayerFromId(activeActions, drainActionId, drainAttackId).getPosition();

        if(sourceActionId < 0)
            sourceCell = getPlayerFromId(activeActions, -1 - sourceActionId, sourceAttackId).getHistoricPosition();

        if(!sourceCell.isAligned(drainCell))
            throw new ConstraintNotSatisfiedException("The two players are not aligned.");
    }
}
