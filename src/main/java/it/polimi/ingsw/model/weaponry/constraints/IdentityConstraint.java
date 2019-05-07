package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.player.ActiveAction;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public class IdentityConstraint extends Constraint {
    private boolean same;

    public IdentityConstraint(int sourceActionId, int sourceAttackId, int drainActionId, int drainAttackId, boolean same) {
        this.sourceActionId = sourceActionId;
        this.sourceAttackId = sourceAttackId;
        this.drainActionId = drainActionId;
        this.drainAttackId = drainAttackId;
        this.same = same;
        this.type = ConstraintType.IDENTITY;
    }

    @Override
    public void verify(List<ActiveAction> activeActions) throws ConstraintNotSatisfiedException {
        Player sourcePlayer = getPlayerFromId(activeActions, sourceActionId, sourceAttackId);
        Player drainPlayer = getPlayerFromId(activeActions, drainActionId, drainAttackId);

        if(sourcePlayer.equals(drainPlayer) != same)
            throw new ConstraintNotSatisfiedException("The two players are " + (same ? "not" : "") + " the same.");
    }
}
