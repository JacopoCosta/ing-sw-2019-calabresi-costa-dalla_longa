package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link RoomConstraint} may require that two {@link Room}s be the same in order to satisfy the constraint.
 * Alternatively, it may instead require two {@link Room}s not to be the same, depending on the truth attribute.
 */
public class RoomConstraint extends Constraint {
    /**
     * Whether or not the two {@link Room}s should actually be the same {@link Room} in order to satisfy the constraint.
     */
    private boolean truth;

    /**
     * This is the only constructor.
     * @param sourceAttackModuleId the id of the {@link AttackModule} containing the source target.
     * @param sourceTargetId the id of the source target.
     * @param drainAttackModuleId the id of the {@link AttackModule} containing the drain target.
     * @param drainTargetId the id of the drain target.
     * @param truth the truth value needed from the predicate asserting equality between {@link Room}s, in order to satisfy the constraint.
     */
    RoomConstraint(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId, boolean truth) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.truth = truth;
        this.type = ConstraintType.ROOM;
    }

    /**
     * Tells if the constraint is satisfied by two given {@link Room}s.
     * @param sourceRoom the source {@link Room}.
     * @param drainRoom the drain {@link Room}.
     * @return true if the {@link Room}s satisfy the constraint, false if they don't or if any of them is null.
     */
    private boolean verify(Room sourceRoom, Room drainRoom) {
        if(sourceRoom == null || drainRoom == null)
            return false;
        return sourceRoom.equals(drainRoom) == truth;
    }

    /**
     * Creates a list of all players that satisfy the constraint.
     * @param context the {@link AttackPattern} in which the constraint is relevant.
     * @return the list of players.
     */
    @Override
    public List<Player> filterPlayers(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Room drainRoom = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getRoom();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> {
                        if(p.getPosition() != null)
                            return this.verify(p.getPosition().getRoom(), drainRoom);
                        return false;
                    })
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Room sourceRoom = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getRoom();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> {
                        if(p.getPosition() != null)
                            return this.verify(sourceRoom, p.getPosition().getRoom());
                        return false;
                    })
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    /**
     * Creates a list of all cells that satisfy the constraint.
     * @param context the {@link AttackPattern} in which the constraint is relevant.
     * @return the list of cells.
     */
    @Override
    public List<Cell> filterCells(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Room drainRoom = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getRoom();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .filter(c -> this.verify(c.getRoom(), drainRoom))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Room sourceRoom = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getRoom();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .filter(c -> this.verify(sourceRoom, c.getRoom()))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    /**
     * Creates a list of all {@link Room}s that satisfy the constraint.
     * @param context the {@link AttackPattern} in which the constraint is relevant.
     * @return the list of {@link Room}s.
     */
    @Override
    public List<Room> filterRooms(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Room drainRoom = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getRoom();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .map(Cell::getRoom)
                    .sorted()
                    .filter(r -> this.verify(r, drainRoom))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Room sourceRoom = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getRoom();

            return context.getAuthor()
                    .getGame()
                    .getBoard()
                    .getCells()
                    .stream()
                    .map(Cell::getRoom)
                    .sorted(Comparator.comparing(Room::toString))
                    .filter(r -> this.verify(sourceRoom, r))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    @Override
    public String toString() {
        String s = getHumanReadableName(sourceAttackModuleId, sourceTargetId) + " ";
        s += truth ? "is (in) the same room as" : "is not (in) the same room as";
        s += " " + getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
