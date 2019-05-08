package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.player.ActiveAction;

import java.util.List;

public class SameRoomContraint extends Constraint {
    private boolean same;

    public SameRoomContraint(int sourceActionId, int sourceAttackId, int drainActionId, int drainAttackId, boolean same) {
        this.sourceActionId = sourceActionId;
        this.sourceAttackId = sourceAttackId;
        this.drainActionId = drainActionId;
        this.drainAttackId = drainAttackId;
        this.same = same;
        this.type = ConstraintType.SAMEROOM;
    }

    @Override
    public void verify(List<ActiveAction> activeActions) throws ConstraintNotSatisfiedException {
        Room sourceRoom = getPlayerFromId(activeActions, sourceActionId, sourceAttackId).getPosition().getRoom();
        Room drainRoom = getPlayerFromId(activeActions, drainActionId, drainAttackId).getPosition().getRoom();

        if(sourceRoom.equals(drainRoom) != same)
            throw new ConstraintNotSatisfiedException("The two players are" + (same ? " not" : "") + "in the same room.");
    }
}
