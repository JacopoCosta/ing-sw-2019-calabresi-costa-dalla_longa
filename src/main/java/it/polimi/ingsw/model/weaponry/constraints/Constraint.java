package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.exceptions.InvalidConstraintTypeException;
import it.polimi.ingsw.model.exceptions.InvalidEffectTypeException;
import it.polimi.ingsw.model.player.ActiveAction;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

import java.util.List;

public abstract class Constraint {
    protected int sourceActionId;
    protected int sourceAttackId;
    protected int drainActionId;
    protected int drainAttackId;
    protected ConstraintType type;

    public static Constraint build(DecoratedJSONObject jConstraint) throws InvalidConstraintTypeException {
        int sourceActionId = jConstraint.getInt("sourceActionId");
        int sourceAttackId = jConstraint.getInt("sourceAttackId");
        int drainActionId = jConstraint.getInt("drainActionId");
        int drainAttackId = jConstraint.getInt("drainAttackId");
        String type = jConstraint.getString("type");

        if(type.equals("alignment")) {
            return new AlignmentConstraint(sourceActionId, sourceAttackId, drainActionId, drainAttackId);
        }
        if(type.equals("distance")) {
            int lowerBound = jConstraint.getInt("lowerBound");
            int upperBound = jConstraint.getInt("upperBound");
            return new DistanceConstraint(sourceActionId, sourceAttackId, drainActionId, drainAttackId, lowerBound, upperBound);
        }
        if(type.equals("identity")) {
            boolean same = jConstraint.getBoolean("same");
            return new IdentityConstraint(sourceActionId, sourceAttackId, drainActionId, drainAttackId, same);
        }
        if(type.equals("order")) {
            int gateActionId = jConstraint.getInt("gateActionId");
            int gateAttackId = jConstraint.getInt("gateAttackId");
            return new OrderConstraint(sourceActionId, sourceAttackId, gateActionId, gateAttackId, drainActionId, drainAttackId);
        }
        if(type.equals("sameRoom")) {
            boolean same = jConstraint.getBoolean("same");
            return new SameRoomContraint(sourceActionId, sourceAttackId, drainActionId, drainAttackId, same);
        }
        if(type.equals("visibility")) {
            boolean sees = jConstraint.getBoolean("sees");
            return new VisibilityConstraint(sourceActionId, sourceAttackId, drainActionId, drainAttackId, sees);
        }
        throw new InvalidEffectTypeException(type + " is not a valid name for an Effect type. Use \"alignment\", \"distance\", \"identity\", \"order\", \"sameRoom\", or \"visibility\"");
    }

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
