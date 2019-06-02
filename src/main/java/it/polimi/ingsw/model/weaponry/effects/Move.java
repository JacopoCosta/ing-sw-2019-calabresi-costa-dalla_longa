package it.polimi.ingsw.model.weaponry.effects;


import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.view.virtual.VirtualView;

public class Move extends Effect {
    protected int sourceAttackModuleId;
    protected int sourceTargetId;
    protected int drainAttackModuleId;
    protected int drainTargetId;

    Move(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.type = EffectType.MOVE;
    }

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
