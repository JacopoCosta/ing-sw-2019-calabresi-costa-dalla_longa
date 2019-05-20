package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;

import java.util.List;
import java.util.stream.Collectors;

public class RoomConstraint extends Constraint {
    private boolean truth;

    public RoomConstraint(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId, boolean truth) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.truth = truth;
        this.type = ConstraintType.ROOM;
    }

    private boolean verify(Room sourceRoom, Room drainRoom) {
        return sourceRoom.equals(drainRoom) == truth;
    }

    @Override
    public List<Player> filterPlayers(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Room drainRoom = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getRoom();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> this.verify(p.getPosition().getRoom(), drainRoom))
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
                    .filter(p -> this.verify(sourceRoom, p.getPosition().getRoom()))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

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
                    .sorted()
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
