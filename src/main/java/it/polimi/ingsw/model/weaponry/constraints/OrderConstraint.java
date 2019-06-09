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
 * An order constraint requires that three cells be aligned and in the order listed.
 * Since this is a ternary constraint, it requires an intermediary entity to make a complete statement.
 * It is necessary (but not sufficient) that the intermediary be either in the middle or share at least one
 * extremity with either of the other two cells, or both of them.
 * @see Cell#isBetween(Cell, Cell)
 */
public class OrderConstraint extends Constraint {
    /**
     * The id of the attack module in which the intermediary is located.
     */
    private int gateAttackModuleId;

    /**
     * The id of the target representing the intermediary.
     */
    private int gateTargetId;

    /**
     * This is the only constructor.
     * @param sourceAttackModuleId the id of the attack module containing the source target.
     * @param sourceTargetId the id of the source target.
     * @param gateAttackModuleId the id of the attack module containing the gate target.
     * @param gateTargetId the id of the gate target.
     * @param drainAttackModuleId the id of the attack module containing the drain target.
     * @param drainTargetId the id of the drain target.
     */
    OrderConstraint(int sourceAttackModuleId, int sourceTargetId, int gateAttackModuleId, int gateTargetId, int drainAttackModuleId, int drainTargetId) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.gateAttackModuleId = gateAttackModuleId;
        this.gateTargetId = gateTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.type = ConstraintType.ORDER;
    }

    /**
     * Tells if the constraint is satisfied by two given cells.
     * @param sourceCell the source cell.
     * @param gateCell the gate cell.
     * @param drainCell the drain cell.
     * @return true if the cells satisfy the constraint, false if they don't or if any of them is null.
     */
    private boolean verify(Cell sourceCell, Cell gateCell, Cell drainCell) {
        if(gateCell == null)
            return false;
        try {
            return gateCell.isBetween(sourceCell, drainCell);
        } catch (NullCellOperationException e) {
            return false;
        }
    }

    /**
     * Creates a list of all players that satisfy the constraint.
     * @param context the attack pattern in which the constraint is relevant.
     * @return the list of players.
     */
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

    /**
     * Creates a list of all cells that satisfy the constraint.
     * @param context the attack pattern in which the constraint is relevant.
     * @return the list of cells.
     */
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

    /**
     * Since the order constraint is not defined on rooms, this method instantly
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
