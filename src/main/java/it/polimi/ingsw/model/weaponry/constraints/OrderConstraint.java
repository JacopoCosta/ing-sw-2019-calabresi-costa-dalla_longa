package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.player.ActiveAction;

import java.util.List;

public class OrderConstraint extends Constraint {
    private int gateActionId;
    private int gateAttackId;

    public OrderConstraint(int sourceActionId, int sourceAttackId, int gateActionId, int gateAttackId, int drainActionId, int drainAttackId) {
        this.sourceActionId = sourceActionId;
        this.sourceAttackId = sourceAttackId;
        this.gateActionId = gateActionId;
        this.gateAttackId = gateAttackId;
        this.drainActionId = drainActionId;
        this.drainAttackId = drainAttackId;
        this.type = ConstraintType.ORDER;
    }

    @Override
    public void verify(List<ActiveAction> activeActions) throws ConstraintNotSatisfiedException {
        Cell sourceCell = getPlayerFromId(activeActions, sourceActionId, sourceAttackId).getPosition();
        Cell gateCell = getPlayerFromId(activeActions, gateActionId, gateAttackId).getPosition();
        Cell drainCell = getPlayerFromId(activeActions, drainActionId, drainAttackId).getPosition();

        if(!gateCell.isBetween(sourceCell, drainCell))
            throw new ConstraintNotSatisfiedException("The three cells are either not aligned or in the wrong order.");
    }
}
