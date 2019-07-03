package it.polimi.ingsw.model.weaponry.effects;


import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.view.virtual.VirtualView;

/**
 * {@link Move}s are {@link Effect}s inflicted by a {@link Player} to themselves or to other {@link Player}s. A {@link Move} causes a {@link Player} to change position.
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
     * @param sourceAttackModuleId the id of the {@link AttackModule} for the source of the {@link Constraint} for the {@link Effect}.
     * @param sourceTargetId the id of the {@link Target} for the source of the {@link Constraint} for the {@link Effect}.
     * @param drainAttackModuleId the id of the {@link AttackModule} for the drain of the {@link Constraint} for the {@link Effect}.
     * @param drainTargetId the id of the {@link Target} for the drain of the {@link Constraint} for the {@link Effect}.
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
     * Moves one or more {@link Player}s to another {@link Player} or to another {@link Cell}, based on the properties.
     */
    @Override
    public void apply() {
        Player target = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getPlayer();
        Cell destination = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();
        target.setPosition(destination);

        VirtualView virtualView = author.getGame().getVirtualView();
        virtualView.announceMove(target, author, destination);
    }

    /**
     * Creates a short description of the effect.
     * @return the description.
     */
    @Override
    public String toString() {
        String s = "move " + Constraint.getHumanReadableName(sourceAttackModuleId, sourceTargetId);
        s+= " to " + Constraint.getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
