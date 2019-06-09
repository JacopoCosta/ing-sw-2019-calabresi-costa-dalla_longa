package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A distance constraint requires that the distance separating two cells be within a specific interval
 * of values in order for either cell to be eligible for being chosen as a target.
 * @see Cell#distance(Cell)
 */
public class DistanceConstraint extends Constraint {
    /**
     * The minimum distance required to pass the constraint test.
     */
    private int lowerBound;

    /**
     * The maximum distance required to pass the constraint test, or -1 to disable upper bound checks,
     * allowing arbitrarily large distances through.
     */
    private int upperBound;

    /**
     * This is the only constructor.
     * @param sourceAttackModuleId the id of the attack module containing the source target.
     * @param sourceTargetId the id of the source target.
     * @param drainAttackModuleId the id of the attack module containing the drain target.
     * @param drainTargetId the id of the drain target.
     * @param lowerBound the minimum distance between source and drain, in order to satisfy the constraint.
     * @param upperBound the maximum distance between source and drain, in order to satisfy the constraint.
     */
    DistanceConstraint(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId, int lowerBound, int upperBound) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.type = ConstraintType.DISTANCE;
    }

    /**
     * Tells if the constraint is satisfied by two given cells.
     * @param sourceCell the source cell.
     * @param drainCell the drain cell.
     * @return true if the cells satisfy the constraint, false if they don't or if any of them is null.
     */
    private boolean verify(Cell sourceCell, Cell drainCell) {
        if(sourceCell == null)
            return false;
        int distance;
        try {
            distance = sourceCell.distance(drainCell);
        } catch (NullCellOperationException e) {
            return false;
        }
        return (distance >= lowerBound) && (distance <= upperBound || upperBound < 0);
    }

    /**
     * Creates a list of all players that satisfy the constraint.
     * @param context the attack pattern in which the constraint is relevant.
     * @return the list of players.
     */
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

    /**
     * Creates a list of all cells that satisfy the constraint.
     * @param context the attack pattern in which the constraint is relevant.
     * @return the list of cells.
     */
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

    /**
     * Since the distance constraint is not defined on rooms, this method instantly
     * throws an {@code InvalidFilterInvocationException} at runtime, since calling
     * this method on an instance of this class is symptom of logical flaws in the
     * constraint management workflow. This method was implemented anyway in order
     * to reduce the number of explicit casts and to be able to factorize it in
     * the {@code Constraint} superclass, where it is declared as abstract.
     * @param context the attack pattern in which the constraint is relevant.
     * @return nothing.
     */
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
