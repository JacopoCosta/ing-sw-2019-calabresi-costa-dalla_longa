package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;

import java.util.List;
import java.util.stream.Collectors;

public class OrderConstraint extends Constraint {
    private int gateAttackModuleId;
    private int gateTargetId;

    OrderConstraint(int sourceAttackModuleId, int sourceTargetId, int gateAttackModuleId, int gateTargetId, int drainAttackModuleId, int drainTargetId) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.gateAttackModuleId = gateAttackModuleId;
        this.gateTargetId = gateTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.type = ConstraintType.ORDER;
    }

    private boolean verify(Cell sourceCell, Cell gateCell, Cell drainCell) {
        if(gateCell == null)
            return false;
        try {
            return gateCell.isBetween(sourceCell, drainCell);
        } catch (NullCellOperationException e) {
            return false;
        }
    }

    @Override
    public List<Player> filterPlayers(AttackPattern context) {
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
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    @Override
    public List<Cell> filterCells(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Cell gateCell = Constraint.getTarget(context, gateAttackModuleId, gateTargetId).getCell();
            Cell drainCell = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .filter(c -> this.verify(c, gateCell, drainCell))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(gateAttackModuleId == -3 && gateTargetId == -3) {
            Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();
            Cell drainCell = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .filter(c -> this.verify(sourceCell, c, drainCell))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();
            Cell gateCell = Constraint.getTarget(context, gateAttackModuleId, gateTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .filter(c -> this.verify(sourceCell, gateCell, c))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    @Override
    public List<Room> filterRooms(AttackPattern context) {
        throw new InvalidFilterInvocationException("A room can't be \"between\".");
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
