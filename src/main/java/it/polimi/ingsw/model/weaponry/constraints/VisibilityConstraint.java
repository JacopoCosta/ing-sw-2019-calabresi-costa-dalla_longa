package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.targets.Target;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link VisibilityConstraint} requires that a cell be visible from another. More specifically,
 * the drain must be visible from the source.
 *
 * @see Cell#canSee(Cell)
 */
public class VisibilityConstraint extends Constraint {
    private boolean truth;

    /**
     * This is the only constructor.
     *
     * @param sourceAttackModuleId the id of the {@link AttackModule} containing the source {@link Target}.
     * @param sourceTargetId       the id of the source {@link Target}.
     * @param drainAttackModuleId  the id of the {@link AttackModule} containing the drain {@link Target}.
     * @param drainTargetId        the id of the drain {@link Target}.
     * @param truth                the truth value needed from the visibility predicate, in order to satisfy the {@link Constraint}.
     */
    VisibilityConstraint(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId, boolean truth) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.truth = truth;
        this.type = ConstraintType.VISIBILITY;
    }

    /**
     * Tells if the {@link Constraint} is satisfied by two given cells.
     *
     * @param sourceCell the source cell.
     * @param drainCell  the drain cell.
     * @return true if the cells satisfy the {@link Constraint}, false if they don't or if any of them is null.
     */
    private boolean verify(Cell sourceCell, Cell drainCell) {
        if(sourceCell == null)
            return false;
        try {
            return sourceCell.canSee(drainCell) == truth;
        } catch (NullCellOperationException e) {
            return false;
        }
    }

    /**
     * Tells if the {@link Constraint} is satisfied by a cell and any other cell found inside a {@link Room}.
     *
     * @param sourceCell the cell.
     * @param drainRoom  the {@link Room}.
     * @return true if and only if the cell satisfies the {@link Constraint} as the source, with any of the cells inside the {@link Room}
     * acting as the drain.
     */
    private boolean verify(Cell sourceCell, Room drainRoom) {
        return drainRoom.getCells()
                .stream()
                .anyMatch(c -> {
                    try {
                        return sourceCell.canSee(c);
                    } catch (NullCellOperationException e) {
                        return false;
                    }
                });
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
     * Creates a list of all cells that satisfy the {@link Constraint}.
     *
     * @param context the {@link AttackPattern} in which the {@link Constraint} is relevant.
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
     * Creates a list of all {@link Room}s that satisfy the {@link Constraint}.
     *
     * @param context the {@link AttackPattern} in which the {@link Constraint} is relevant.
     * @return the list of {@link Room}s.
     */
    @Override
    public List<Room> filterRooms(AttackPattern context) {
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Cell sourceCell = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getCell();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .map(Cell::getRoom)
                    .filter(r -> this.verify(sourceCell, r))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    /**
     * Generates a short human-readable string summarizing what the {@link Constraint} requires.
     * @return the string.
     */
    @Override
    public String toString() {
        String s = getHumanReadableName(sourceAttackModuleId, sourceTargetId) + " ";
        s += truth ? "can see" : "cannot see";
        s += " " + getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
