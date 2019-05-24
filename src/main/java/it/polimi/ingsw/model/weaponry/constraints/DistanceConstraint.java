package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.DistanceFromNullException;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;

import java.util.List;
import java.util.stream.Collectors;

public class DistanceConstraint extends Constraint {
    private int lowerBound;
    private int upperBound;

    public DistanceConstraint(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId, int lowerBound, int upperBound) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.type = ConstraintType.DISTANCE;
    }

    private boolean verify(Cell sourceCell, Cell drainCell) {
        int distance;
        try {
            distance = sourceCell.distance(drainCell);
        } catch (DistanceFromNullException e) {
            return false;
        }
        return (distance >= lowerBound) && (distance <= upperBound || upperBound < 0);
    }

    @Override
    public List<Player> filterPlayers(AttackPattern context) {
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
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    @Override
    public List<Cell> filterCells(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Cell drainCell = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .filter(c -> this.verify(c, drainCell))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .filter(c -> this.verify(sourceCell, c))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    @Override
    public List<Room> filterRooms(AttackPattern context) {
        throw new InvalidFilterInvocationException("A room can't have \"distance\".");
    }

    @Override
    public String toString() {
        String s = getHumanReadableName(sourceAttackModuleId, sourceTargetId) + " ";
        s += upperBound >= 0 ? ("is " + lowerBound + "~" + upperBound + " away from") : ("is at least " + lowerBound + " away from");
        s += " " + getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
