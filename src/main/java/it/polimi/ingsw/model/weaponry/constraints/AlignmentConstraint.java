package it.polimi.ingsw.model.weaponry.constraints;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;

import java.util.List;
import java.util.stream.Collectors;

public class AlignmentConstraint extends Constraint {
    private boolean truth;

    public AlignmentConstraint(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId, boolean truth) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.truth = truth;
        this.type = ConstraintType.ALIGNMENT;
    }

    private boolean verify(Cell sourceCell, Cell drainCell) {
        return sourceCell.isAligned(drainCell) == truth;
    }

    @Override
    public boolean verify() {
        Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();
        Cell drainCell = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();

        return verify(sourceCell, drainCell);
    }

    @Override
    public List<Player> filter(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Cell drainCell = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();
            
            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> this.verify(p.getPosition(), drainCell))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> this.verify(sourceCell, p.getPosition()))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new IllegalStateException("This instance of constraint can't use a filter.");
    }

    @Override
    public String toString() {
        String s = getHumanReadableName(sourceAttackModuleId, sourceTargetId) + " ";
        s += truth ? "is aligned with" : "is not aligned with";
        s += " " + getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
