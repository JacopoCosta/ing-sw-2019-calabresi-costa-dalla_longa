package it.polimi.ingsw.model.weaponry.effects;


import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.Dispatcher;

public class Move extends Effect {
    protected int sourceAttackModuleId;
    protected int sourceTargetId;
    protected int drainAttackModuleId;
    protected int drainTargetId;

    public Move(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.type = EffectType.MOVE;
    }

    @Override
    public void apply() {
        Player target = Constraint.getTarget(context, sourceAttackModuleId, drainTargetId).getPlayer();
        Cell destination = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();

        Dispatcher.sendMessage(target.getName() + " moves to " + destination.getId() + ".\n");
        target.setPosition(destination);
    }

    @Override
    public String toString() {
        String s = "move " + Constraint.getHumanReadableName(sourceAttackModuleId, sourceTargetId);
        s+= " to " + Constraint.getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
