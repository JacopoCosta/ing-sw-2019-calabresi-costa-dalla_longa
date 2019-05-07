package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.player.ActiveAction;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public abstract class Constraint {
    protected int sourceActionId;
    protected int sourceAttackId;
    protected int drainActionId;
    protected int drainAttackId;
    protected ConstraintType type;

    // given attackId, it returns a player, based on the following rules:
    // 0 is the author of all of the action's attacks
    // 1 is the target of the 1st attack of the action
    // 2 is the target of the 2nd attack of the action
    // ... and so on
    protected Player getPlayerFromId(List<ActiveAction> activeActions, int actionId, int attackId) {
        if(attackId == 0)
            return activeActions.get(actionId).getAction().getAttacks().get(0).getAuthor();
        return activeActions.get(actionId).getAction().getAttacks().get(attackId - 1).getTarget();
    }

    public abstract void verify(List<ActiveAction> activeActions) throws ConstraintNotSatisfiedException;
}
