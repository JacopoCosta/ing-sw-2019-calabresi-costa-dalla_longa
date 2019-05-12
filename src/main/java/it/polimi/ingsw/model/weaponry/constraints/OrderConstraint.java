package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;
import it.polimi.ingsw.model.weaponry.targets.TargetType;

import java.util.List;
import java.util.stream.Collectors;

public class OrderConstraint extends Constraint {
    private int gateAttackModuleId;
    private int gateTargetId;

    public OrderConstraint(int sourceAttackModuleId, int sourceTargetId, int gateAttackModuleId, int gateTargetId, int drainAttackModuleId, int drainTargetId) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.gateAttackModuleId = gateAttackModuleId;
        this.gateTargetId = gateTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.type = ConstraintType.ORDER;
    }

    private boolean verify(Cell sourceCell, Cell gateCell, Cell drainCell) {
        return gateCell.isBetween(sourceCell, drainCell);
    }

    @Override
    public boolean verify() {
        Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();
        Cell gateCell = Constraint.getTarget(context, gateAttackModuleId, gateTargetId).getCell();
        Cell drainCell = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();

        return verify(sourceCell, gateCell, drainCell);
    }

    @Override
    public List<Player> filter(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Cell gateCell = Constraint.getTarget(context, gateAttackModuleId, gateTargetId).getCell();
            Cell drainCell = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> this.verify(p.getPosition(), gateCell, drainCell))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(gateAttackModuleId == -3 && gateTargetId == -3) {
            Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();
            Cell drainCell = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> this.verify(sourceCell, p.getPosition(), drainCell))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();
            Cell gateCell = Constraint.getTarget(context, gateAttackModuleId, gateTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> this.verify(sourceCell, gateCell, p.getPosition()))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new IllegalStateException("This instance of constraint can't use a filter.");
    }

    @Override
    public String toString() {
        String s = getHumanReadableName(gateAttackModuleId, gateTargetId) + " ";
        s += "is between";
        s += " " + getHumanReadableName(sourceAttackModuleId, sourceTargetId) + " ";
        s += "and";
        s += " " + getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
