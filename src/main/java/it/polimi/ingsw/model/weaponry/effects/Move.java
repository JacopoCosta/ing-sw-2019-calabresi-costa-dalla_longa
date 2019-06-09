package it.polimi.ingsw.model.weaponry.effects;


import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.virtual.VirtualView;

/**
 * Moves are effects inflicted by a player to themselves or to other players. A move causes a player to change position.
 */
public class Move extends Effect {
    /**
     * Works exactly like the {@code sourceAttackModule} property of {@code Constraint}.
     * @see Constraint
     */
    protected int sourceAttackModuleId;

    /**
     * Works exactly like the {@code sourceTargetId} property of {@code Constraint}.
     * @see Constraint
     */
    protected int sourceTargetId;

    /**
     * Works exactly like the {@code drainAttackModuleId} property of {@code Constraint}.
     * @see Constraint
     */
    protected int drainAttackModuleId;

    /**
     * Works exactly like the {@code drainTargetId} property of {@code Constraint}.
     * @see Constraint
     */
    protected int drainTargetId;

    /**
     * This is the only constructor
     * @param sourceAttackModuleId the id of the attack module for the source of the constraint for the effect.
     * @param sourceTargetId the id of the target for the source of the constraint for the effect.
     * @param drainAttackModuleId the id of the attack module for the drain of the constraint for the effect.
     * @param drainTargetId the id of the target for the drain of the constraint for the effect.
     * @see Move#sourceAttackModuleId
     * @see Move#sourceTargetId
     * @see Move#drainAttackModuleId
     * @see Move#drainTargetId
     * @see Constraint
     */
    Move(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.type = EffectType.MOVE;
    }

    /**
     * Moves one or more players to another player or to another cell, based on the properties.
     */
    @Override
    public void apply() {
        Player target = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getPlayer();
        Cell destination = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();
        target.setPosition(destination);

        VirtualView virtualView = author.getGame().getVirtualView();
        virtualView.announceMove(author, target, destination);
    }

    @Override
    public String toString() {
        String s = "move " + Constraint.getHumanReadableName(sourceAttackModuleId, sourceTargetId);
        s+= " to " + Constraint.getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
