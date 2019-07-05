package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.targets.Target;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link OrderConstraint} requires that three cells be aligned and in the order listed.
 * Since this is a ternary {@link Constraint}, it requires an intermediary entity to make a complete statement.
 * It is necessary (but not sufficient) that the intermediary be either in the middle or share at least one
 * extremity with either of the other two cells, or both of them.
 *
 * @see Cell#isBetween(Cell, Cell)
 */
public class OrderConstraint extends Constraint {
    /**
     * The id of the {@link AttackModule} in which the intermediary is located.
     */
    private int gateAttackModuleId;

    /**
     * The id of the {@link Target} representing the intermediary.
     */
    private int gateTargetId;

    /**
     * This is the only constructor.
     *
     * @param sourceAttackModuleId the id of the {@link AttackModule} containing the source {@link Target}.
     * @param sourceTargetId       the id of the source {@link Target}.
     * @param gateAttackModuleId   the id of the {@link AttackModule} containing the gate {@link Target}.
     * @param gateTargetId         the id of the gate {@link Target}.
     * @param drainAttackModuleId  the id of the {@link AttackModule} containing the drain {@link Target}.
     * @param drainTargetId        the id of the drain {@link Target}.
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
     * Tells if the {@link Constraint} is satisfied by two given cells.
     *
     * @param sourceCell the source cell.
     * @param gateCell   the gate cell.
     * @param drainCell  the drain cell.
     * @return true if the cells satisfy the {@link Constraint}, false if they don't or if any of them is null.
     */
    private boolean verify(Cell sourceCell, Cell gateCell, Cell drainCell) {
        if (gateCell == null)
            return false;
        try {
            return gateCell.isBetween(sourceCell, drainCell);
        } catch (NullCellOperationException e) {
            return false;
        }
    }

    /**
     * Creates a list of all {@link Player}s that satisfy the {@link Constraint}.
     *
     * @param context the {@link AttackPattern} in which the {@link Constraint} is relevant.
     * @return the list of {@link Player}s.
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
     * Creates a list of all cells that satisfy the {@link Constraint}.
     *
     * @param context the {@link AttackPattern} in which the {@link Constraint} is relevant.
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
     * Since the {@link OrderConstraint} is not defined on {@link Room}s, this method instantly
     * throws an {@code InvalidFilterInvocationException} at runtime, since calling
     * this method on an instance of this class is symptom of logical flaws in the
     * {@link Constraint} management workflow. This method was implemented anyway in order
     * to reduce the number of explicit casts and to be able to factorize it in
     * the {@code Constraint} superclass, where it is declared as abstract.
     *
     * @param context the {@link AttackPattern} in which the {@link Constraint} is relevant.
     * @return nothing.
     */
    @Override
    public List<Room> filterRooms(AttackPattern context) {
        throw new InvalidFilterInvocationException("A room can't be \"between\".");
    }

    /**
     * Generates a short human-readable string summarizing what the {@link Constraint} requires.
     * @return the string.
     */
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
