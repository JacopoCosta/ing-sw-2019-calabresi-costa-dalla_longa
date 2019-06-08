package it.polimi.ingsw.model.weaponry.effects;


import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.virtual.VirtualView;

/**
 * Moves are effects inflicted by a player to themselves or to other players. It causes a player to change position.
 */
public class Move extends Effect {
    /**
     * -3 if it applies to everyone.
     * -2 if it applies to the attacker in the position they had before starting their current execution.
     * -1 if it applies to the attacker in their current position.
     * The id of the {@code AttackModule} inside the {@code AttackPattern} containing the {@code Target}
     * from which the effect will start, in every other case.
     */
    protected int sourceAttackModuleId;

    /**
     * -3 if it applies to everyone.
     * -2 if it applies to the attacker in the position they had before starting their current execution.
     * -1 if it applies to the attacker in their current position.
     * The id of the {@code Target} from which the effect will start, in every other case.
     */
    protected int sourceTargetId;

    /**
     * -3 if it applies to everyone.
     * -2 if it applies to the attacker in the position they had before starting their current execution.
     * -1 if it applies to the attacker in their current position.
     * The id of the {@code AttackModule} inside the {@code AttackPattern} containing the {@code Target}
     * on which the effect will end, in every other case.
     */
    protected int drainAttackModuleId;

    /**
     * -3 if it applies to everyone.
     * -2 if it applies to the attacker in the position they had before starting their current execution.
     * -1 if it applies to the attacker in their current position.
     * The id of the {@code Target} on which the effect will end, in every other case.
     */
    protected int drainTargetId;

    /**
     * This is the only constructor
     * @param sourceAttackModuleId -3 if it applies to everyone.
     * -2 if it applies to the attacker in the position they had before starting their current execution.
     * -1 if it applies to the attacker in their current position.
     * The id of the {@code AttackModule} inside the {@code AttackPattern} containing the {@code Target}
     * from which the effect will start, in every other case.
     * @param sourceTargetId -3 if it applies to everyone.
     * -2 if it applies to the attacker in the position they had before starting their current execution.
     * -1 if it applies to the attacker in their current position.
     * The id of the {@code Target} from which the effect will start, in every other case.
     * @param drainAttackModuleId -3 if it applies to everyone.
     * -2 if it applies to the attacker in the position they had before starting their current execution.
     * -1 if it applies to the attacker in their current position.
     * The id of the {@code AttackModule} inside the {@code AttackPattern} containing the {@code Target}
     * on which the effect will end, in every other case.
     * @param drainTargetId -3 if it applies to everyone.
     * -2 if it applies to the attacker in the position they had before starting their current execution.
     * -1 if it applies to the attacker in their current position.
     * The id of the {@code Target} on which the effect will end, in every other case.
     */
    Move(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.type = EffectType.MOVE;
    }

    /**
     * Moves one or more players to another player or to another cell.
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
