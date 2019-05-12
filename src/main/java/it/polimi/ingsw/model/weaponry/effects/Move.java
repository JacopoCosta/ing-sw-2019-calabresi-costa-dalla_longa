package it.polimi.ingsw.model.weaponry.effects;


import it.polimi.ingsw.model.weaponry.constraints.Constraint;

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
    }

    @Override
    public String toString() {
        String s = "move " + Constraint.getHumanReadableName(sourceAttackModuleId, sourceTargetId);
        s+= " to " + Constraint.getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
